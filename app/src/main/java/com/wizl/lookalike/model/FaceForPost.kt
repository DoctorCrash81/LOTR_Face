package com.wizl.lookalike.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class FaceForPost (
    val result: ArrayList<FaceResult>
    ): Serializable {

    fun getJPG(index: Int): String
        {
            return if (index < result.count())   result[index].jpg_data
                    else                     ""
        }

    fun getHeroName(index: Int): String
    {
        return if (index < result.count())   result[index].category
        else                     ""
    }
    }

data class FaceResult(
    @SerializedName("jpg_data")
    val jpg_data: String,
    @SerializedName("category")
    val category: String): Serializable
