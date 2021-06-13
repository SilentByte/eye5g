/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.silentbyte.eye5g.databinding.ActivityMainBinding
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import java.io.ByteArrayOutputStream

private const val TAG = "MainActivity"
private const val INFERENCE_IMAGE_SIZE = 608

class MainActivity : AppActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var isDetecting = false
    private lateinit var detectionWebSocket: DetectionWebSocket
    private lateinit var detectionSpeaker: DetectionSpeaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)

        detectionWebSocket = DetectionWebSocket().also {
            it.connectionOpenedCallback = {
                detectionSpeaker.speak(R.string.speak_detection_started)
            }

            it.connectionClosedCallback = {
                if(isDetecting) {
                    detectionSpeaker.speak(R.string.speak_reconnecting)
                    detectionWebSocket.open(getAppPreferences().serviceUrl)
                } else {
                    detectionSpeaker.speak(R.string.speak_detection_stopped)
                }
            }

            it.connectionErrorCallback = {
                detectionSpeaker.speak(R.string.speak_connection_error)
                stopDetection()
            }

            it.detectionCallback = { objects ->
                Log.d(TAG, "Detection Received: $objects")
                detectionSpeaker.addObjects(objects)
            }
        }

        detectionSpeaker = DetectionSpeaker(this)

        binding.fab.setOnClickListener {
            if(!hasAllPermissions()) {
                requestAllPermissions()
            } else {
                if(isDetecting) {
                    stopDetection()
                } else {
                    startDetection()
                }
            }
        }

        loadStateFromAppPreferences(getAppPreferences())
        requestAllPermissions()
    }

    override fun onPause() {
        super.onPause()
        stopDetection()
    }

    override fun onDestroy() {
        super.onDestroy()

        detectionWebSocket.close()
        detectionSpeaker.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_settings -> {
                showSettingsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences?, key: String?) {
        loadStateFromAppPreferences(getAppPreferences(preferences))

        if(key == "locale") {
            recreate()
        }
    }

    private fun getAppPreferences(preferences: SharedPreferences? = null): AppPreferences {
        return if(preferences == null) {
            AppPreferences(PreferenceManager.getDefaultSharedPreferences(this))
        } else {
            AppPreferences(preferences)
        }
    }

    private fun showSettingsActivity() {
        startActivityForResult(Intent(this, SettingsActivity::class.java), 0)
    }

    private fun requestAllPermissions() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.permission_rationale),
            PERMISSION_REQUEST_CODE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
        )
    }

    private fun hasAllPermissions() =
        EasyPermissions.hasPermissions(
            this,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA
        )

    private fun loadStateFromAppPreferences(preferences: AppPreferences) {
        preferences.also { p ->
            appLocale = p.locale
            detectionSpeaker.also {
                it.locale = p.locale
                it.speechRate = p.speechRate
                it.maxAge = 3.0f
            }
        }

        updateAppConfig()
    }

    @Suppress("unused")
    @AfterPermissionGranted(PERMISSION_REQUEST_CODE)
    private fun onPermissionGranted() {
        if(hasAllPermissions()) {
            Log.i(TAG, "Permissions have been granted")
        } else {
            requestAllPermissions()
        }
    }

    private fun startDetection() {
        binding.fab.setImageResource(R.drawable.ic_eye_on)

        detectionWebSocket.open(getAppPreferences().serviceUrl)
        isDetecting = true
    }

    private fun stopDetection() {
        binding.fab.setImageResource(R.drawable.ic_eye_off)

        isDetecting = false
        detectionWebSocket.close()
    }

    private var testDebounceLastTime = 0L
    private var testDebounceCheckTime = 0L

    // TODO: Implement properly, dynamically adjust based on priority and latency.
    private fun testDebounce(): Boolean {
        testDebounceCheckTime = System.nanoTime()
        return if(testDebounceCheckTime > testDebounceLastTime + 200_000_000) {
            testDebounceLastTime = testDebounceCheckTime
            true
        } else {
            false
        }
    }

    fun onPreviewUpdated(render: () -> Bitmap?) {
        if(!isDetecting) {
            return
        }

        if(!detectionWebSocket.isConnected || !testDebounce()) {
            return
        }

        Log.e(TAG, "Sending frame for detection.")
        render()?.let { bitmap ->
            ByteArrayOutputStream().use { stream ->
                Bitmap
                    .createScaledBitmap(bitmap, INFERENCE_IMAGE_SIZE, INFERENCE_IMAGE_SIZE, true)
                    .compress(Bitmap.CompressFormat.JPEG, 90, stream)

                detectionWebSocket.send(stream.toByteArray())
            }
        }
    }
}
