/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.silentbyte.eye5g.databinding.FragmentCameraBinding

private const val TAG = "CameraFragment"

class CameraFragment : Fragment(), TextureView.SurfaceTextureListener {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var camera: Camera? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        val preview = binding.previewTextureView
        if(preview.isAvailable) {
            startCamera(preview.surfaceTexture!!)
        } else {
            preview.surfaceTextureListener = this
        }
    }

    private fun startCamera(surface: SurfaceTexture) {
        try {
            camera = Camera.open()
            camera?.also {
                it.setPreviewTexture(surface)
                it.setDisplayOrientation(90)
                it.parameters = it.parameters.also { p ->
                    p.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                    p.setPreviewSize(640, 480)
                }
                it.startPreview()
            }
        } catch(e: Exception) {
            // TODO: Handle error.
            Log.e(TAG, "Could not access camera", e)
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, p1: Int, p2: Int) {
        startCamera(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, p1: Int, p2: Int) {
        //
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        camera?.also {
            it.stopPreview()
            it.release()
        }
        camera = null
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        //
    }
}