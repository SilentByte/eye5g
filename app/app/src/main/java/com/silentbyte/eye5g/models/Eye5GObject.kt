/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g.models

data class Eye5GObject(
    val label: String,
    val probability: Float,
    val bbox: BBox,
)
