package com.example.cinephile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(private val movies: List<Movie>) :
    RecyclerView.Adapter<MovieAdapter.MovieRowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieRowViewHolder {
        // Create a horizontal LinearLayout to hold three cards
        val rowLayout = LinearLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
        }

        // Add three movie items to the row
        repeat(3) {
            val movieView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_movie, rowLayout, false)
            rowLayout.addView(movieView)
        }

        return MovieRowViewHolder(rowLayout)
    }

    override fun onBindViewHolder(holder: MovieRowViewHolder, position: Int) {
        val startIndex = position * 3
        for (i in 0..2) {
            val movieIndex = startIndex + i
            val movieView = holder.movieViews[i]

            if (movieIndex < movies.size) {
                movieView.visibility = View.VISIBLE
                val movie = movies[movieIndex]

                val titleView = movieView.findViewById<TextView>(R.id.movieTitle)
                val posterView = movieView.findViewById<ImageView>(R.id.moviePoster)

                titleView.text = movie.title
                Glide.with(movieView.context)
                    .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                    .into(posterView)
            } else {
                movieView.visibility = View.INVISIBLE
            }
        }
    }

    override fun getItemCount(): Int = (movies.size + 2) / 3 // Ceiling division

    class MovieRowViewHolder(view: LinearLayout) : RecyclerView.ViewHolder(view) {
        val movieViews: List<View> = (0 until 3).map { view.getChildAt(it) }
    }
}