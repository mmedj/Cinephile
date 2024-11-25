package com.example.cinephile

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {

    // Define the endpoint for fetching popular movies
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<TmdbMovieResponse>
}
