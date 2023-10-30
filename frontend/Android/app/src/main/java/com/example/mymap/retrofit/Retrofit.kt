package com.example.mymap.retrofit

import android.content.Context
import java.time.Instant
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Retrofit {
    companion object {
        private const val BASE_URL = "https://friends-app-b7tv.onrender.com/"
        private var retrofit: Retrofit? = null
        fun getRetrofitInstance(context: Context): Retrofit {
            if (retrofit == null) {
                val contentType = "application/json".toMediaType()
                val gson = GsonBuilder().registerTypeAdapter(
                    Instant::class.java,
                    InstantTypeAdapter()
                ).create()
                retrofit =
                    Retrofit.Builder().baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(okhttpClient(context))
                        .build()
            }
            return retrofit!!
        }

        private fun okhttpClient(context: Context): OkHttpClient {
            return OkHttpClient.Builder()
//                .addInterceptor(AuthInterceptor(context))
                .build()
        }
    }
}

suspend fun <T : Any?> Call<T>.enqueue() = suspendCoroutine<Response<T>> { cont ->
    this.enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            cont.resumeWithException(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            cont.resume(response)
        }
    })
}