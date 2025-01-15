package com.example.cinephile.Retrofit

import com.example.cinephile.Services.FlaskApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FlaskRetrofitInstance {
    private const val BASE_URL = "https://4eae-2a04-cec0-1047-c62c-3582-7fcb-675-d76b.ngrok-free.app" // Use 10.0.2.2 for Android emulator

    val apiService: FlaskApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlaskApiService::class.java)
    }
}