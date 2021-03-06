package com.joe.forecast.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.joe.forecast.data.network.response.CurrentWeatherResponse
import com.joe.forecast.data.network.response.FutureWeatherResponse
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "b37d56eab5e24f7ca7c50711190504"

// https://api.apixu.com/v1/current.json?key=b37d56eab5e24f7ca7c50711190504&q=Juba&lang=en

interface ApixuWeatherApiService {

    @GET("current.json")
    fun getCurrentWeatherAsync(
        @Query("q") location: String,
        @Query("lang") languageCode: String = "en"
    ): Deferred<CurrentWeatherResponse>

    // https://api.apixu.com/v1/forecast.json?key=b37d56eab5e24f7ca7c50711190504&q=Nairobi&days=1
    @GET("forecast.json")
    fun getFutureWeatherAsync(
        @Query("q") location: String,
        @Query("days") days: Int,
        @Query("lang") languageCode: String = "en"
    ): Deferred<FutureWeatherResponse>

    companion object {
        operator fun invoke(connectivityInterceptor: ConnectivityInterceptor): ApixuWeatherApiService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request().url().newBuilder().addQueryParameter("key", API_KEY).build()
                val request = chain.request().newBuilder().url(url).build()
                return@Interceptor chain.proceed(request)
            }

            val okHttpClient =
                OkHttpClient.Builder().addInterceptor(requestInterceptor).addInterceptor(connectivityInterceptor)
                    .build()

            return Retrofit.Builder().client(okHttpClient).baseUrl("https://api.apixu.com/v1/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory()).addConverterFactory(GsonConverterFactory.create())
                .build().create(ApixuWeatherApiService::class.java)
        }
    }
}
