package com.wizl.lookalike.logick.net

import android.os.Handler
import com.wizl.lookalike.App
import com.wizl.lookalike.model.FaceForPost
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class Network {

    private var mServerApi: IRequest? = null

    private object Holder {
        var INSTANCE = Network()
    }

    companion object {

        private const val HOST_IMG = "https://celebritybabymaker.com/"
        private const val HOST_API_V1 = "https://celebritybabymaker.com/api/v1/"

        val instance: Network by lazy { Holder.INSTANCE }
    }

    fun initApi() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
        val mRetrofit = Retrofit.Builder()
            .baseUrl(HOST_API_V1)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        mServerApi = mRetrofit.create(IRequest::class.java)
    }

    fun getResult(
        file: File,
        gender: Boolean,
        onResponse: (FaceForPost) -> Unit,
        onFailure: (t: Throwable) -> Unit,
        onServerError: (code: Int, message: String) -> Unit
    ) {
        val headers = HashMap<String, String>()
        val handler = Handler()
        headers["accept-language"] = App.instance.language

        val photoFileRB =
            RequestBody.create(
                "multipart/form-data".toMediaTypeOrNull(),
                file
            )

        val photoFileParameter =
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                photoFileRB
            )

        val strGender = if (gender) "LOTR_male" else "LOTR_female"

        val dataset = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),strGender)
        val count = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),"3")

        Thread {
            mServerApi?.getFacesFromServer(headers,photoFileParameter,dataset, count)
                ?.enqueue(object :
                    Callback<FaceForPost> {
                    override fun onFailure(call: Call<FaceForPost>, t: Throwable) {
                        handler.post {
                            onFailure(t)
                        }
                    }

                    override fun onResponse(
                        call: Call<FaceForPost>,
                        response: Response<FaceForPost>
                    ) {
                        when {
                            response.body() != null && response.code() == 200 -> handler.post {
                                onResponse(response.body()!!)
                            }
                            else -> handler.post {
                                onServerError(response.code(), response.message())
                            }
                        }
                    }

                })
        }.start()
    }
}