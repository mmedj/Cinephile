package com.example.cinephile.dataClass

import com.example.cinephile.dataClass.Genre
import com.example.cinephile.dataClass.Movie

data class MovieResponse(
    val results: List<Movie>
)
data class GenreResponse(
    val genres: List<Genre>
)
