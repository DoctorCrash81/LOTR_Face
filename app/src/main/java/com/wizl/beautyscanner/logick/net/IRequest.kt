package com.wizl.beautyscanner.logick.net

import com.wizl.beautyscanner.model.BeautyModelSerializable
import com.wizl.beautyscanner.model.FaceForPost
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IRequest {

    val lang: String
        get() = ""

    @Multipart
//    @POST("beauty_scanner/")
    @POST("face_swap/batch")
    fun getBeautyByPhoto(
        @HeaderMap headers: Map<String, String>,
        @Part file: MultipartBody.Part,

        // Дополнил
    //    @Part("dataset") _dataset: String,
    //    @Part("count") _count: String
        @Part("dataset") _dataset: RequestBody,
        @Part("count") _count: RequestBody
        ): Call<FaceForPost>

//    ): Call<BeautyModelSerializable>

}