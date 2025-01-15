package com.example.cinephile.Services

import com.example.cinephile.dataClass.SimilarMovie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlaskApiService {
    @GET("recommend")
    fun getRecommendedMovies(
            @Query("movie_id") movieId: Int
    ): Call<List<SimilarMovie>>
}
