package com.wizl.beautyscanner.ui

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.wizl.beautyscanner.logick.helpers.DisplayHelper
import com.wizl.beautyscanner.model.FaceRectSerializable

class DrawView : View {
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    )
            : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    )
            : super(context, attrs, defStyleAttr, defStyleRes)

    private var p: Paint = Paint()
    private var rect: Rect = Rect()

    private var x11 = 0f
    private var y11 = 0f
    private var x12 = 0f
    private var y12 = 0f

    private var x21 = 0f
    private var y21 = 0f
    private var x22 = 0f
    private var y22 = 0f


    private var x31 = 0f
    private var y31 = 0f
    private var x32 = 0f
    private var y32 = 0f

    private var x41 = 0f
    private var y41 = 0f
    private var x42 = 0f
    private var y42 = 0f

    override fun onDraw(canvas: Canvas) {

        p.color = Color.RED
        p.strokeWidth = DisplayHelper.dpToPx(1).toFloat()

        canvas.drawLine(x11, y11, x11, y12, p)
        canvas.drawLine(x11, y12, x12, y12, p)
        canvas.drawCircle(x11, y11, DisplayHelper.dpToPx(2).toFloat(), p)

        val x20 = x21 + ((x22 - x21) / 2f).toInt().toFloat()
        canvas.drawLine(x21, y21, x20, y21, p)
        canvas.drawLine(x20, y21, x20, y22, p)
        canvas.drawLine(x20, y22, x22, y22, p)
        canvas.drawCircle(x21, y21, DisplayHelper.dpToPx(2).toFloat(), p)

        val x30 = x31 + ((x32 - x31) / 2f).toInt().toFloat()
        canvas.drawLine(x31, y31, x30, y31, p)
        canvas.drawLine(x30, y31, x30, y32, p)
        canvas.drawLine(x30, y32, x32, y32, p)
        canvas.drawCircle(x31, y31, DisplayHelper.dpToPx(2).toFloat(), p)

        canvas.drawLine(x41, y41, x41, y42, p)
        canvas.drawLine(x41, y42, x42, y42, p)
        canvas.drawCircle(x41, y41, DisplayHelper.dpToPx(2).toFloat(), p)

        canvas.drawRect(rect, p)
    }

    fun set1(x1: Float, y1: Float, x2: Float, y2: Float) {
        x11 = x1
        y11 = y1
        x12 = x2
        y12 = y2
    }

    fun set2(x1: Float, y1: Float, x2: Float, y2: Float) {
        x21 = x1
        y21 = y1
        x22 = x2
        y22 = y2
    }

    fun set3(x1: Float, y1: Float, x2: Float, y2: Float) {
        x31 = x1
        y31 = y1
        x32 = x2
        y32 = y2
    }

    fun set4(x1: Float, y1: Float, x2: Float, y2: Float) {
        x41 = x1
        y41 = y1
        x42 = x2
        y42 = y2
    }


}