package com.example.cinephile.dataClass

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
    val rating: Float,
    val release_date: String
)
data class SimilarMovie(
    val id: Int,
    val title: String,
    val overview: String?,
    val poster_path: String?,
    val genres: String,
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


data class MovieCreditsResponse(
    val cast: List<Cast>,
    val crew: List<Crew>
)

data class Cast(
    val name: String,
    val character: String,
    val profile_path: String?
)

data class Crew(
    val name: String,
    val job: String,
    val profile_path: String?
)

data class ProductionCompany(
    val id: Int,
    val logo_path: String?,
    val name: String,
    val origin_country: String
)

