package com.example.cinephile

data class Genre(
    val id: Int,
    val name: String
)

data class Movie(
    val id: Int,
    val title: String,
    val overview: String?,
    val poster_path: String?,
    val genre_ids: List<Int>,
    val vote_average: Double,
    val rating: Float
)

data class MovieDetailsResponse(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val genres: List<Genre>,
    val release_date: String,
    val runtime: Int?,
    val vote_average: Double,
    val vote_count: Int,
    val budget: Int?,
    val revenue: Int?,
    val homepage: String?,
    val tagline: String?,
    val production_companies: List<ProductionCompany>
)



data class ProductionCompany(
    val id: Int,
    val logo_path: String?,
    val name: String,
    val origin_country: String
)

