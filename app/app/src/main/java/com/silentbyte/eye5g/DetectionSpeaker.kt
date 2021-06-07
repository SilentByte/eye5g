/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.StringRes
import com.silentbyte.eye5g.models.Eye5GObject
import java.io.Closeable

private const val TAG = "DetectionSpeaker"

private fun objectComparator(lhs: Eye5GObject, rhs: Eye5GObject): Int {
    var result = -lhs.priority.compareTo(rhs.priority)
    if(result != 0) {
        return result
    }

    result = -lhs.probability.compareTo(rhs.probability)
    if(result != 0) {
        return result
    }

    return lhs.age.compareTo(rhs.age)
}

class DetectionSpeaker(private val context: Context) : Closeable {
    private var isInitialized = false
    private val queue = ArrayList<Eye5GObject>()

    var maxAge = 2.0f
        set(value) {
            field = value.coerceAtLeast(0.0f)
        }

    var speechRate = 1.0f
        set(value) {
            tts.setSpeechRate(value)
            field = value
        }

    private val tts = TextToSpeech(context) { status ->
        isInitialized = status == TextToSpeech.SUCCESS
    }

    private fun speak(text: String) {
        Log.i(TAG, "Speaking: $text")
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun speak(@StringRes resId: Int, vararg formatArgs: Any) {
        speak(context.getString(resId, formatArgs))
    }

    fun addObjects(objects: Iterable<Eye5GObject>) {
        if(tts.isSpeaking) {
            return
        }

        val originalGroup = queue
            .filter { it.age < maxAge }
            .groupBy { it.label }

        val group = objects
            .filter { it.age < maxAge }
            .sortedWith { lhs, rhs -> objectComparator(lhs, rhs) }
            .groupBy { it.label }

        queue.clear()

        val announceGroup = LinkedHashMap<String, MutableList<Eye5GObject>>()
        for(g in group) {
            // If a new object type (with label as the key) has been detected,
            // or if the number of objects of the same type has changed,
            // schedule that group for announcement.
            if(g.value.size != originalGroup[g.key]?.size) {
                queue.addAll(g.value)
                announceGroup.computeIfAbsent(g.key) {
                    mutableListOf()
                }.addAll(g.value)
            }
        }

        if(announceGroup.isEmpty()) {
            return
        }

        val text = announceGroup.entries.joinToString(", ") {
            context.resources.getQuantityString(it.value[0].nameResId, it.value.size, it.value.size)
        }

        speak(text)
    }

    override fun close() {
        tts.shutdown()
    }
}
