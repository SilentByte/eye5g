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
import com.google.android.material.snackbar.Snackbar
import com.silentbyte.eye5g.databinding.ActivityMainBinding
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    companion object {
        const val PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            if(!hasAllPermissions()) {
                requestAllPermissions()
            } else {
                // TODO: Open WebSocket connection.
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

    @AfterPermissionGranted(PERMISSION_REQUEST_CODE)
    private fun onPermissionGranted() {
        if(hasAllPermissions()) {
            Log.i(TAG, "Permissions have been granted")
        } else {
            requestAllPermissions()
        }
    }
}
