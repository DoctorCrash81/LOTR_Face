package com.wizl.beautyscanner.logick.net

import com.wizl.beautyscanner.model.BeautyModelSerializable
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IRequest {

    val lang: String
        get() = ""

    @Multipart
    @POST("beauty_scanner/")
    fun getBeautyByPhoto(
        @HeaderMap headers: Map<String, String>,
        @Part file: MultipartBody.Part
    ): Call<BeautyModelSerializable>

}