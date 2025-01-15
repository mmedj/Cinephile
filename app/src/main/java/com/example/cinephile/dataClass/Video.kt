package com.example.cinephile.dataClass
data class MovieVideosResponse(
    val results: List<Video>
)

data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
)
