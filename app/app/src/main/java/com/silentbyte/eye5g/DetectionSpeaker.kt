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
import com.silentbyte.eye5g.models.Eye5GObjectLocation
import java.io.Closeable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

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

    var maxObjectCount = 8
        set(value) {
            field = value.coerceAtLeast(1)
        }

    var speechRate = 1.0f
    var locale: Locale? = null

    private val tts = TextToSpeech(context) { status ->
        isInitialized = status == TextToSpeech.SUCCESS
    }

    private fun updateConfig() {
        if(!isInitialized) {
            return
        }

        tts.setSpeechRate(speechRate)
        locale?.let { tts.language = it }
    }

    private fun speak(text: String) {
        Log.i(TAG, "Speaking: $text")

        updateConfig()
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    fun speak(@StringRes resId: Int, vararg formatArgs: Any) {
        speak(context.getString(resId, formatArgs))
    }

    private fun announce(
        group: Map<String, List<Eye5GObject>>,
        originalGroup: Map<String, List<Eye5GObject>>,
        location: Eye5GObjectLocation,
    ) {
        val announceGroup = LinkedHashMap<String, MutableList<Eye5GObject>>()
        for(g in group) {
            // If a new object type (with label as the key) has been detected,
            // or if the number of objects of the same type has changed,
            // schedule that group for announcement.
            if(g.value.size != originalGroup[g.key]?.size) {
                announceGroup.computeIfAbsent(g.key) {
                    mutableListOf()
                }.addAll(g.value)
            }
        }

        if(announceGroup.isEmpty()) {
            return
        }

        val objectText = announceGroup.entries.joinToString(", ") {
            context.resources.getQuantityString(it.value[0].nameResId, it.value.size, it.value.size)
        }

        val locationText = when(location) {
            Eye5GObjectLocation.Left -> context.getString(R.string.speak_location_left)
            Eye5GObjectLocation.Center -> context.getString(R.string.speak_location_center)
            Eye5GObjectLocation.Right -> context.getString(R.string.speak_location_right)
        }

        speak("$objectText, $locationText")
    }

    fun addObjects(objects: Iterable<Eye5GObject>) {
        if(tts.isSpeaking) {
            return
        }

        val originalObjects = queue
            .filter { it.age < maxAge }
            .sortedWith { lhs, rhs -> objectComparator(lhs, rhs) }

        val filteredObjects = objects
            .filter { it.age < maxAge }
            .sortedWith { lhs, rhs -> objectComparator(lhs, rhs) }
            .take(maxObjectCount)


        for(location in arrayOf(
            Eye5GObjectLocation.Center,
            Eye5GObjectLocation.Left,
            Eye5GObjectLocation.Right,
        )) {
            announce(
                filteredObjects
                    .filter { it.location == location }
                    .groupBy { it.label },
                originalObjects
                    .filter { it.location == location }
                    .groupBy { it.label },
                location,
            )
        }

        queue.clear()
        queue.addAll(objects)
    }

    override fun close() {
        tts.shutdown()
    }
}
