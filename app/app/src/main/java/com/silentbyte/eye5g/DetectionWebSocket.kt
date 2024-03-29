/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g

import android.util.Log
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.WebSocket
import com.silentbyte.eye5g.models.BBox
import com.silentbyte.eye5g.models.Eye5GObject
import org.json.JSONArray
import org.json.JSONException

private const val TAG = "DetectionWebSocket"

typealias DetectionCallback = (objects: MutableList<Eye5GObject>) -> Unit
typealias ConnectionOpenedCallback = () -> Unit
typealias ConnectionClosedCallback = () -> Unit
typealias ConnectionErrorCallback = () -> Unit

class DetectionWebSocket {
    private var ws: WebSocket? = null

    var detectionCallback: DetectionCallback? = null
    var connectionOpenedCallback: ConnectionOpenedCallback? = null
    var connectionClosedCallback: ConnectionClosedCallback? = null
    var connectionErrorCallback: ConnectionErrorCallback? = null

    val isConnected: Boolean
        get() = ws?.isOpen == true

    private fun receiveString(payload: String) {
        if(detectionCallback == null) {
            return
        }

        try {
            val data = JSONArray(payload)
            val objects = mutableListOf<Eye5GObject>()

            for(i in 0 until data.length()) {
                val detection = data.getJSONObject(i)
                objects.add(Eye5GObject(
                    label = detection.getString("label"),
                    probability = detection.getDouble("probability").toFloat(),
                    bbox = detection.getJSONObject("bbox").let {
                        BBox(
                            x = it.getDouble("x").toFloat(),
                            y = it.getDouble("y").toFloat(),
                            width = it.getDouble("width").toFloat(),
                            height = it.getDouble("height").toFloat(),
                        )
                    },
                ))
            }

            detectionCallback?.invoke(objects)
        } catch(e: JSONException) {
            Log.e(TAG, "Received invalid JSON response", e)
        }
    }

    fun open(url: String) {
        if(ws != null) {
            throw IllegalStateException("WebSocket connection has already been opened")
        }

        AsyncHttpClient.getDefaultInstance().websocket(url, null) { ex, socket ->
            if(ex != null) {
                Log.e(TAG, "Failed top en WebSocket connection", ex)
                connectionErrorCallback?.invoke()
                return@websocket
            }

            ws = socket?.also {
                it.setClosedCallback {
                    Log.i(TAG, "WebSocket has been closed")
                    ws = null
                    connectionClosedCallback?.invoke()
                }

                it.setStringCallback { payload ->
                    Log.i(TAG, "WebSocket received string data")
                    receiveString(payload)
                }

                it.setDataCallback { _, bb ->
                    Log.w(TAG, "WebSocket received unexpected binary data, ignoring...")
                    bb.recycle()
                }
            }

            connectionOpenedCallback?.invoke()
        }
    }

    fun close() {
        // TODO: Check how to send end-frame to close the connection properly.
        ws?.end()
    }

    fun send(data: ByteArray) {
        if(ws == null) {
            throw IllegalStateException("WebSocket connection is not open")
        }

        ws?.send(data)
    }
}
