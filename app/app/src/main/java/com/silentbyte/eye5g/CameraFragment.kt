/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.silentbyte.eye5g.databinding.FragmentCameraBinding

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null

    private val binding get() = _binding!!

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
}