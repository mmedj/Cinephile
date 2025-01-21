package com.example.cinephile.Services

import com.example.cinephile.dataClass.GenreResponse
import com.example.cinephile.dataClass.MovieCreditsResponse
import com.example.cinephile.dataClass.MovieDetailsResponse
import com.example.cinephile.dataClass.MovieResponse
import com.example.cinephile.dataClass.MovieVideosResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String): Call<MovieResponse>
    @GET("discover/movie")
    fun discoverMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/now_playing") // Example for another category
    fun getNowPlayingMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<MovieDetailsResponse>
    @GET("movie/{movie_id}/credits")
    fun getMovieCredits(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<MovieCreditsResponse>
    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call< MovieVideosResponse>
    // Search for movies based on a query
    @GET("search/movie")
    fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): Call<MovieResponse>

    // Search for TV shows based on a query
    @GET("search/tv")
    fun searchTvShows(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): Call<MovieResponse>
    @GET("search/movie")
    fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): Call<MovieResponse>

    // New endpoint to fetch all genres
    @GET("genre/movie/list")
    fun getGenres(@Query("api_key") apiKey: String): Call<GenreResponse>
    @GET("discover/movie")
    fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: String,
        @Query("vote_average.gte") minRating: Double? = null, // Minimum rating
        @Query("vote_average.lte") maxRating: Double? = null, // Maximum rating
        @Query("primary_release_year") releaseYear: Int? // Optional year filter
    ): Call<MovieResponse>

}
