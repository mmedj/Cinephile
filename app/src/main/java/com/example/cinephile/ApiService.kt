package com.example.cinephile

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/now_playing") // Example for another category
    fun getNowPlayingMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    // New endpoint to fetch all genres
    @GET("genre/movie/list")
    fun getGenres(@Query("api_key") apiKey: String): Call<GenreResponse>
    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<MovieDetailsResponse>
}
