/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.annotation.StringRes
import com.silentbyte.eye5g.R

class DetectionSpeaker(private val context: Context) {
    private var isInitialized = false
    private var isActive = false

    private val tts = TextToSpeech(context) { status ->
        isInitialized = status == TextToSpeech.SUCCESS
    }

    private fun enqueue(text: CharSequence) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    private fun speak(@StringRes resId: Int) {
        tts.speak(context.getString(resId), TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun start() {
        this.speak(R.string.speak_detection_started)
        isActive = true
    }

    fun stop() {
        this.speak(R.string.speak_detection_stopped)
        isActive = false
    }
}
