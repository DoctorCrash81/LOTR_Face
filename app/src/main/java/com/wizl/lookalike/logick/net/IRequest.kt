package com.wizl.lookalike.logick.net

import com.wizl.lookalike.model.FaceForPost
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IRequest {

    @Multipart
    @POST("face_swap/batch")
    fun getFacesFromServer(
        @HeaderMap headers: Map<String, String>,
        @Part file: MultipartBody.Part,

        // Дополнил
        @Part("dataset") _dataset: RequestBody,
        @Part("count") _count: RequestBody
        ): Call<FaceForPost>

}