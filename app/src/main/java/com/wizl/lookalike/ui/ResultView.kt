package com.wizl.lookalike.ui

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import com.wizl.lookalike.R

class ResultView : LinearLayout {
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

    private var x0 = -1f
    private var y0 = -1f

    private fun removeOnGlobalLayoutListener0() {
        viewTreeObserver.removeOnGlobalLayoutListener(listener0)
    }

    private val listener0 = ViewTreeObserver.OnGlobalLayoutListener {
        val location = IntArray(2)
        getLocationOnScreen(location)
        x0 = location[0].toFloat()
        y0 = location[1].toFloat()
        updateLine()
        removeOnGlobalLayoutListener0()
    }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.view_bs_result, this, true)

        viewTreeObserver.addOnGlobalLayoutListener(listener0)
    }

    private fun updateLine() {
    }
}