/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g.models

import android.graphics.Rect
import android.graphics.RectF
import androidx.annotation.PluralsRes
import com.silentbyte.eye5g.R

data class LabelDetail(
    val priority: Int,
    @PluralsRes val nameResId: Int,
)

private val labelMap = mapOf(
    "bus" to LabelDetail(100, R.plurals.speak_object_bus),
    "car" to LabelDetail(100, R.plurals.speak_object_car),
    "motorbike" to LabelDetail(100, R.plurals.speak_object_motorbike),
    "train" to LabelDetail(100, R.plurals.speak_object_train),
    "truck" to LabelDetail(100, R.plurals.speak_object_truck),

    "bicycle" to LabelDetail(70, R.plurals.speak_object_bicycle),
    "dog" to LabelDetail(70, R.plurals.speak_object_dog),
    "horse" to LabelDetail(70, R.plurals.speak_object_horse),
    "person" to LabelDetail(70, R.plurals.speak_object_person),
    "traffic light" to LabelDetail(70, R.plurals.speak_object_traffic_light),
    "zebra" to LabelDetail(70, R.plurals.speak_object_zebra),

    "backpack" to LabelDetail(50, R.plurals.speak_object_backpack),
    "bed" to LabelDetail(50, R.plurals.speak_object_bed),
    "bench" to LabelDetail(50, R.plurals.speak_object_bench),
    "book" to LabelDetail(50, R.plurals.speak_object_book),
    "bottle" to LabelDetail(50, R.plurals.speak_object_bottle),
    "bowl" to LabelDetail(50, R.plurals.speak_object_bowl),
    "cell phone" to LabelDetail(50, R.plurals.speak_object_cell_phone),
    "chair" to LabelDetail(50, R.plurals.speak_object_chair),
    "clock" to LabelDetail(50, R.plurals.speak_object_clock),
    "diningtable" to LabelDetail(50, R.plurals.speak_object_dining_table),
    "fire hydrant" to LabelDetail(50, R.plurals.speak_object_fire_hydrant),
    "hair drier" to LabelDetail(50, R.plurals.speak_object_hair_dryer),
    "keyboard" to LabelDetail(50, R.plurals.speak_object_keyboard),
    "laptop" to LabelDetail(50, R.plurals.speak_object_laptop),
    "microwave" to LabelDetail(50, R.plurals.speak_object_microwave),
    "mouse" to LabelDetail(50, R.plurals.speak_object_mouse),
    "oven" to LabelDetail(50, R.plurals.speak_object_oven),
    "parking meter" to LabelDetail(50, R.plurals.speak_object_parking_meter),
    "pottedplant" to LabelDetail(50, R.plurals.speak_object_potted_plant),
    "refrigerator" to LabelDetail(50, R.plurals.speak_object_refrigerator),
    "remote" to LabelDetail(50, R.plurals.speak_object_remote),
    "scissors" to LabelDetail(50, R.plurals.speak_object_scissors),
    "sink" to LabelDetail(50, R.plurals.speak_object_sink),
    "sofa" to LabelDetail(50, R.plurals.speak_object_sofa),
    "sports ball" to LabelDetail(50, R.plurals.speak_object_sports_ball),
    "stop sign" to LabelDetail(50, R.plurals.speak_object_stop_sign),
    "teddy bear" to LabelDetail(50, R.plurals.speak_object_teddy_bear),
    "toaster" to LabelDetail(50, R.plurals.speak_object_toaster),
    "toilet" to LabelDetail(50, R.plurals.speak_object_toilet),
    "toothbrush" to LabelDetail(50, R.plurals.speak_object_toothbrush),
    "tvmonitor" to LabelDetail(50, R.plurals.speak_object_tv_monitor),
    "vase" to LabelDetail(50, R.plurals.speak_object_vase),

    "aeroplane" to LabelDetail(20, R.plurals.speak_object_aeroplane),
    "apple" to LabelDetail(20, R.plurals.speak_object_apple),
    "banana" to LabelDetail(20, R.plurals.speak_object_banana),
    "baseball bat" to LabelDetail(20, R.plurals.speak_object_baseball_bat),
    "baseball glove" to LabelDetail(20, R.plurals.speak_object_baseball_glove),
    "bear" to LabelDetail(20, R.plurals.speak_object_bear),
    "bird" to LabelDetail(20, R.plurals.speak_object_bird),
    "boat" to LabelDetail(20, R.plurals.speak_object_boat),
    "broccoli" to LabelDetail(20, R.plurals.speak_object_broccoli),
    "cake" to LabelDetail(20, R.plurals.speak_object_cake),
    "carrot" to LabelDetail(20, R.plurals.speak_object_carrot),
    "cat" to LabelDetail(20, R.plurals.speak_object_cat),
    "cow" to LabelDetail(20, R.plurals.speak_object_cow),
    "cup" to LabelDetail(20, R.plurals.speak_object_cup),
    "donut" to LabelDetail(20, R.plurals.speak_object_donut),
    "elephant" to LabelDetail(20, R.plurals.speak_object_elephant),
    "fork" to LabelDetail(20, R.plurals.speak_object_fork),
    "frisbee" to LabelDetail(20, R.plurals.speak_object_frisbee),
    "giraffe" to LabelDetail(20, R.plurals.speak_object_giraffe),
    "handbag" to LabelDetail(20, R.plurals.speak_object_handbag),
    "hot dog" to LabelDetail(20, R.plurals.speak_object_hot_dog),
    "kite" to LabelDetail(20, R.plurals.speak_object_kite),
    "knife" to LabelDetail(20, R.plurals.speak_object_knife),
    "orange" to LabelDetail(20, R.plurals.speak_object_orange),
    "pizza" to LabelDetail(20, R.plurals.speak_object_pizza),
    "sandwich" to LabelDetail(20, R.plurals.speak_object_sandwich),
    "sheep" to LabelDetail(20, R.plurals.speak_object_sheep),
    "skateboard" to LabelDetail(20, R.plurals.speak_object_skateboard),
    "skis" to LabelDetail(20, R.plurals.speak_object_skis),
    "snowboard" to LabelDetail(20, R.plurals.speak_object_snowboard),
    "spoon" to LabelDetail(20, R.plurals.speak_object_spoon),
    "suitcase" to LabelDetail(20, R.plurals.speak_object_suitcase),
    "surfboard" to LabelDetail(20, R.plurals.speak_object_surfboard),
    "tennis racket" to LabelDetail(20, R.plurals.speak_object_tennis_racket),
    "tie" to LabelDetail(20, R.plurals.speak_object_tie),
    "umbrella" to LabelDetail(20, R.plurals.speak_object_umbrella),
    "wine glass" to LabelDetail(20, R.plurals.speak_object_wine_glass),
)

enum class Eye5GObjectLocation {
    Left,
    Center,
    Right,
}

data class Eye5GObject(
    val label: String,
    val probability: Float,
    val bbox: BBox,
) {
    companion object {
        const val CENTER_WIDTH = 0.6f

        fun intersectionArea(first: RectF, second: RectF): Float {
            val intersection = RectF(first)
            return if(intersection.intersect(second)) {
                intersection.width() * intersection.height()
            } else {
                0.0f
            }
        }
    }

    private val timestamp = System.nanoTime()

    val priority = labelMap[label]?.priority ?: 0

    @PluralsRes
    val nameResId = labelMap[label]?.nameResId ?: R.plurals.speak_object_unknown

    val location: Eye5GObjectLocation

    val age: Float
        get() = (System.nanoTime() - timestamp).toFloat() / 1_000_000_000

    init {
        val objectRect = RectF(
            bbox.x - bbox.width / 2.0f,
            bbox.y - bbox.height / 2.0f,
            bbox.x + bbox.width / 2.0f,
            bbox.y + bbox.height / 2.0f,
        )

        val leftRect = RectF(0.0f, 0.0f, CENTER_WIDTH / 2.0f, 1.0f)
        val centerRect = RectF(CENTER_WIDTH / 2.0f, 0.0f, CENTER_WIDTH + CENTER_WIDTH / 2.0f, 1.0f)
        val rightRect = RectF(CENTER_WIDTH + CENTER_WIDTH / 2.0f, 0.0f, 1.0f, 1.0f)

        location = arrayOf(
            Pair(Eye5GObjectLocation.Left, intersectionArea(leftRect, objectRect)),
            Pair(Eye5GObjectLocation.Center, intersectionArea(centerRect, objectRect)),
            Pair(Eye5GObjectLocation.Right, intersectionArea(rightRect, objectRect)),
        ).maxByOrNull { it.second }!!.first
    }
}
