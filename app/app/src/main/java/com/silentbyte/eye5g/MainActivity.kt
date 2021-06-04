/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.silentbyte.eye5g.databinding.ActivityMainBinding
import com.silentbyte.eye5g.tts.DetectionSpeaker
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var isDetecting = false
    private lateinit var detectionSpeaker: DetectionSpeaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

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

        requestAllPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_settings -> true
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

    @Suppress("unused")
    @AfterPermissionGranted(PERMISSION_REQUEST_CODE)
    private fun onPermissionGranted() {
        if(hasAllPermissions()) {
            Log.i(TAG, "Permissions have been granted")
        } else {
            requestAllPermissions()
        }
    }

    // TODO: Open WebSocket connection, etc.
    private fun startDetection() {
        binding.fab.setImageResource(R.drawable.ic_eye_on)

        detectionSpeaker.start()
        isDetecting = true
    }

    // TODO: Close WebSocket connection, etc.
    private fun stopDetection() {
        binding.fab.setImageResource(R.drawable.ic_eye_off)
        detectionSpeaker.stop()
        isDetecting = false
    }
}
