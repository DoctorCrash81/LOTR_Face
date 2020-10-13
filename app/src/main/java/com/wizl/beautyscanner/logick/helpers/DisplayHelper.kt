package com.wizl.beautyscanner.logick.helpers

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.wizl.beautyscanner.App
import kotlin.math.roundToInt

class DisplayHelper {
    companion object {

        val widthPixels: Int
            get() {
                val displaymetrics = App.instance.resources.displayMetrics
                val metrics = DisplayMetrics()
                val windowManager = App.instance.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.defaultDisplay.getMetrics(metrics)
                val display = windowManager.defaultDisplay
                val metricsB = DisplayMetrics()
                display.getMetrics(metricsB)
                return displaymetrics.widthPixels
            }

        val heightPixels: Int
            get() {
                val displaymetrics = App.instance.resources.displayMetrics
                val metrics = DisplayMetrics()
                val windowManager = App.instance.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.defaultDisplay.getMetrics(metrics)
                val display = windowManager.defaultDisplay
                val metricsB = DisplayMetrics()
                display.getMetrics(metricsB)
                return displaymetrics.heightPixels
            }

        fun dpToPx(dp: Int): Int {
            val displayMetrics = App.instance.resources.displayMetrics
            return (dp * displayMetrics.density).roundToInt()
        }

        fun pxToDp(px: Int): Int {
            val displayMetrics = App.instance.resources.displayMetrics
            return (px / displayMetrics.density).roundToInt()
        }


    }
}