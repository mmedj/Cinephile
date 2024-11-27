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

