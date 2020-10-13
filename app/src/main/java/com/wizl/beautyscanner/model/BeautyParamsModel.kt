package com.wizl.beautyscanner.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BeautyParamsModel(
    var name: String,
    var valui: Float,
    var x: Float,
    var y: Float
) : Parcelable

data class StandartModel(val min: Float, val max: Float, val mean: Float)