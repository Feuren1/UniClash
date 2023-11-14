package project.main.uniclash.retrofit

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.http2.Http2Reader.Companion.logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.String
import java.time.Instant
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
                .addInterceptor(LoggingInterceptor())
                .build()
        }
    }
}
internal class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request: Request = chain.request()
        val t1 = System.nanoTime()
        logger.info(
            String.format(
                "Sending request %s on %s%n%s",
                request.url, chain.connection(), request.headers
            )
        )
        val response: okhttp3.Response = chain.proceed(request)
        val t2 = System.nanoTime()
        logger.info(
            String.format(
                "Received response for %s in %.1fms%n%s",
                response.request.url, (t2 - t1) / 1e6, response.headers
            )
        )
        return response
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