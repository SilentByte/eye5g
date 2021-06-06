/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g.models

private val labelPriorityMap = mapOf(
    "bus" to 100,
    "car" to 100,
    "motorbike" to 100,
    "train" to 100,
    "truck" to 100,

    "bicycle" to 70,
    "dog" to 70,
    "horse" to 70,
    "person" to 70,
    "traffic light" to 70,
    "zebra" to 70,

    "backpack" to 50,
    "bed" to 50,
    "bench" to 50,
    "book" to 50,
    "bottle" to 50,
    "bowl" to 50,
    "cell phone" to 50,
    "chair" to 50,
    "clock" to 50,
    "diningtable" to 50,
    "fire hydrant" to 50,
    "hair drier" to 50,
    "keyboard" to 50,
    "laptop" to 50,
    "microwave" to 50,
    "mouse" to 50,
    "oven" to 50,
    "parking meter" to 50,
    "pottedplant" to 50,
    "refrigerator" to 50,
    "remote" to 50,
    "scissors" to 50,
    "sink" to 50,
    "sofa" to 50,
    "sports ball" to 50,
    "stop sign" to 50,
    "teddy bear" to 50,
    "toaster" to 50,
    "toilet" to 50,
    "toothbrush" to 50,
    "tvmonitor" to 50,
    "vase" to 50,

    "aeroplane" to 20,
    "apple" to 20,
    "banana" to 20,
    "baseball bat" to 20,
    "baseball glove" to 20,
    "bear" to 20,
    "bird" to 20,
    "boat" to 20,
    "broccoli" to 20,
    "cake" to 20,
    "carrot" to 20,
    "cat" to 20,
    "cow" to 20,
    "cup" to 20,
    "donut" to 20,
    "elephant" to 20,
    "fork" to 20,
    "frisbee" to 20,
    "giraffe" to 20,
    "handbag" to 20,
    "hot dog" to 20,
    "kite" to 20,
    "knife" to 20,
    "orange" to 20,
    "pizza" to 20,
    "sandwich" to 20,
    "sheep" to 20,
    "skateboard" to 20,
    "skis" to 20,
    "snowboard" to 20,
    "spoon" to 20,
    "suitcase" to 20,
    "surfboard" to 20,
    "tennis racket" to 20,
    "tie" to 20,
    "umbrella" to 20,
    "wine glass" to 20,
)

data class Eye5GObject(
    val label: String,
    val probability: Float,
    val bbox: BBox,
) {
    private val timestamp = System.nanoTime()

    val priority: Int
        get() = labelPriorityMap[label] ?: 0

    val age: Float
        get() = (System.nanoTime() - timestamp).toFloat() / 1_000_000_000
}
