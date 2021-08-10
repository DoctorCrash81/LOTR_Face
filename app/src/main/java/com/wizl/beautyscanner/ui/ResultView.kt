package com.wizl.beautyscanner.ui

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.wizl.beautyscanner.R
import com.wizl.beautyscanner.logick.helpers.DisplayHelper
import com.wizl.beautyscanner.model.BeautyModel
import com.wizl.beautyscanner.model.BeautyParamsModel
import kotlinx.android.synthetic.main.view_bs_result.view.*
import java.io.File


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

    private var wImg = 0f
    private var hImg = 0f
    private var xImg = -1f
    private var yImg = -1f
    private var x0 = -1f
    private var y0 = -1f
    private var x1 = -1f
    private var y1 = -1f
    private var x2 = -1f
    private var y2 = -1f
    private var x3 = -1f
    private var y3 = -1f
    private var x4 = -1f
    private var y4 = -1f

    private var mBeautyModel: BeautyModel? = null

    private fun removeOnGlobalLayoutListener1() {
        //_oval1.viewTreeObserver.removeOnGlobalLayoutListener(listener1)
    }

    private val listener1 = ViewTreeObserver.OnGlobalLayoutListener {
        val location = IntArray(2)
       // _oval1.getLocationOnScreen(location)
        val x = location[0].toFloat()
        val y = location[1].toFloat()
        x1 = x
        //y1 = y + _oval1.height / 2
        updateLine()
        removeOnGlobalLayoutListener1()
    }

    private fun removeOnGlobalLayoutListener2() {
       // _oval2.viewTreeObserver.removeOnGlobalLayoutListener(listener2)
    }

    private val listener2 = ViewTreeObserver.OnGlobalLayoutListener {
        val location = IntArray(2)
       // _oval2.getLocationOnScreen(location)
        val x = location[0].toFloat()
        val y = location[1].toFloat()
        x2 = x
       // y2 = y + _oval1.height / 2
        updateLine()
        removeOnGlobalLayoutListener2()
    }

    private fun removeOnGlobalLayoutListener3() {
      //  _oval3.viewTreeObserver.removeOnGlobalLayoutListener(listener3)
    }

    private val listener3 = ViewTreeObserver.OnGlobalLayoutListener {
        val location = IntArray(2)
      //  _oval3.getLocationOnScreen(location)
        val x = location[0].toFloat()
        val y = location[1].toFloat()
        x3 = x
       // y3 = y + _oval1.height / 2
        updateLine()
        removeOnGlobalLayoutListener3()
    }

    private fun removeOnGlobalLayoutListener4() {
      //  _oval1.viewTreeObserver.removeOnGlobalLayoutListener(listener4)
    }

    private val listener4 = ViewTreeObserver.OnGlobalLayoutListener {
        val location = IntArray(2)
       // _oval4.getLocationOnScreen(location)
        val x = location[0].toFloat()
        val y = location[1].toFloat()
        x4 = x
       // y4 = y + _oval1.height / 2
        updateLine()
        removeOnGlobalLayoutListener4()
    }

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

    private fun removeOnGlobalLayoutListenerImg() {
        //_imgMain.viewTreeObserver.removeOnGlobalLayoutListener(listenerImg)
    }

    private val listenerImg = ViewTreeObserver.OnGlobalLayoutListener {
        val location = IntArray(2)
       // _imgMain.getLocationOnScreen(location)
        xImg = location[0].toFloat()
        yImg = location[1].toFloat()
       // wImg = _imgMain.width.toFloat()
      //  hImg = _imgMain.height.toFloat()
        updateLine()
        removeOnGlobalLayoutListenerImg()
    }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.view_bs_result, this, true)
/*
        _oval1.viewTreeObserver.addOnGlobalLayoutListener(listener1)
        _oval2.viewTreeObserver.addOnGlobalLayoutListener(listener2)
        _oval3.viewTreeObserver.addOnGlobalLayoutListener(listener3)
        _oval4.viewTreeObserver.addOnGlobalLayoutListener(listener4)*/
        viewTreeObserver.addOnGlobalLayoutListener(listener0)
      //  _imgMain.viewTreeObserver.addOnGlobalLayoutListener(listenerImg)
    }

    fun setBModel(bModel: BeautyModel) {

        mBeautyModel = bModel

        val w = DisplayHelper.widthPixels * 2 / 5
      //  _imgMain.layoutParams.width = w
      //  _imgMain.layoutParams.height = w * 4 / 3
/*
        Glide.with(this)
            .load(File(bModel.pathImg))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .transform(RoundedCorners(DisplayHelper.dpToPx(8)))
            .into(_imgMain)*/
/*
        _txtName1.text = bModel.params[0].name
        _txtName2.text = bModel.params[1].name
        _txtName3.text = bModel.params[2].name
        _txtName4.text = bModel.params[3].name
*/
        val arr = arrayListOf<BeautyParamsModel>(
            bModel.params[0],
            bModel.params[1],
            bModel.params[2],
            bModel.params[3]
        )
        for (i in 0 until arr.size) {
            for (j in 1 until arr.size) {
                if (arr[j - 1].valui < arr[j].valui) {
                    val n = arr[j - 1]
                    arr[j - 1] = arr[j]
                    arr[j] = n
                }
            }
        }

        arr[0].valui =
            if (bModel.score > 94f) 100f
            else ((bModel.score + (99 - bModel.score) / 2) * 100).toInt().toFloat() / 100
        arr[1].valui = ((bModel.score + (99 - bModel.score) / 5) * 100).toInt().toFloat() / 100
        arr[2].valui = ((bModel.score - (99 - bModel.score) / 3) * 100).toInt().toFloat() / 100
        arr[3].valui =
            ((bModel.score * 4 - arr[0].valui - arr[1].valui - arr[2].valui) * 100).toInt()
                .toFloat() / 100
/*
        _txtVolue1.text = "${bModel.params[0].valui.toInt()}%"
        _txtVolue2.text = "${bModel.params[1].valui.toInt()}%"
        _txtVolue3.text = "${bModel.params[2].valui.toInt()}%"
        _txtVolue4.text = "${bModel.params[3].valui.toInt()}%"

        if(bModel.params[0].valui.toInt() == 100){
            _icTrophy1.visibility = View.VISIBLE
            _txtPerfect1.visibility = View.VISIBLE
        } else {
            _icTrophy1.visibility = View.GONE
            _txtPerfect1.visibility = View.GONE
        }
        if(bModel.params[1].valui.toInt() == 100){
            _icTrophy2.visibility = View.VISIBLE
            _txtPerfect2.visibility = View.VISIBLE
        } else {
            _icTrophy2.visibility = View.GONE
            _txtPerfect2.visibility = View.GONE
        }
        if(bModel.params[2].valui.toInt() == 100){
            _icTrophy3.visibility = View.VISIBLE
            _txtPerfect3.visibility = View.VISIBLE
        } else {
            _icTrophy3.visibility = View.GONE
            _txtPerfect3.visibility = View.GONE
        }
        if(bModel.params[3].valui.toInt() == 100){
            _icTrophy4.visibility = View.VISIBLE
            _txtPerfect4.visibility = View.VISIBLE
        } else {
            _icTrophy4.visibility = View.GONE
            _txtPerfect4.visibility = View.GONE
        }

        _txtIndex.text = bModel.score.toString()
*/
        updateLine()

    }

    private fun updateLine() {
        if (mBeautyModel != null && x0 != -1f && x1 != -1f && xImg != -1f && x2 != -1f && x3 != -1f && x4 != -1f) {
            //* wImg/mBeautyModel!!.width.toFloat()
                /*
                _draw.set1(
                mBeautyModel!!.params[0].x * wImg / mBeautyModel!!.width + xImg - x0,
                mBeautyModel!!.params[0].y * hImg / mBeautyModel!!.height + yImg - y0,
                x1 - x0,
                y1 - y0
            )
            _draw.set2(
                mBeautyModel!!.params[1].x * wImg / mBeautyModel!!.width + xImg - x0,
                mBeautyModel!!.params[1].y * hImg / mBeautyModel!!.height + yImg - y0,
                x2 - x0,
                y2 - y0
            )
            _draw.set3(
                mBeautyModel!!.params[2].x * wImg / mBeautyModel!!.width + xImg - x0,
                mBeautyModel!!.params[2].y * hImg / mBeautyModel!!.height + yImg - y0,
                x3 - x0,
                y3 - y0
            )
            _draw.set4(
                mBeautyModel!!.params[3].x * wImg / mBeautyModel!!.width + xImg - x0,
                mBeautyModel!!.params[3].y * hImg / mBeautyModel!!.height + yImg - y0,
                x4 - x0,
                y4 - y0
            )
            _draw.invalidate()*/
        }
    }
}