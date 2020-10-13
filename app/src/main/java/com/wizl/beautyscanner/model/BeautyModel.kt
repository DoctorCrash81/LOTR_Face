package com.wizl.beautyscanner.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BeautyModel(
    var score:Float = 0f,
    var pathImg: String = "",
    var width: Int = 0,
    var height: Int = 0,
    val params: ArrayList<BeautyParamsModel> = arrayListOf()
) : Parcelable {
}
