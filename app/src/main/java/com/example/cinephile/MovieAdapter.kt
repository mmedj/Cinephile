package com.example.cinephile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(
    private val movies: List<Movie>,
    private val genreMap: Map<Int, String> // Pass genreMap to resolve genre IDs to names
) : RecyclerView.Adapter<MovieAdapter.MovieRowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieRowViewHolder {
        // Create a horizontal LinearLayout to hold three movie cards
        val rowLayout = LinearLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
        }

        // Inflate and add three movie items to the row
        repeat(3) {
            val movieView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_movie, rowLayout, false)
            rowLayout.addView(movieView)
        }

        return MovieRowViewHolder(rowLayout)
    }

    override fun onBindViewHolder(holder: MovieRowViewHolder, position: Int) {
        val startIndex = position * 3 // Start index for the current row
        for (i in 0..2) {
            val movieIndex = startIndex + i
            val movieView = holder.movieViews[i]

            if (movieIndex < movies.size) {
                movieView.visibility = View.VISIBLE
                val movie = movies[movieIndex]

                // Find views and bind data
                val titleView = movieView.findViewById<TextView>(R.id.movieTitle)
                val posterView = movieView.findViewById<ImageView>(R.id.moviePoster2)
                val overviewView = movieView.findViewById<TextView>(R.id.movieOverview)
                val ratingBar = movieView.findViewById<RatingBar>(R.id.movieRatingBar)
                val genreView = movieView.findViewById<TextView>(R.id.movieGenres) // Add this in XML if not present

                titleView.text = movie.title
                overviewView.text = movie.overview
                ratingBar.rating = (movie.vote_average / 2).toFloat() // TMDB rating is out of 10, convert to 5 stars

                // Map genres from genre IDs
                val genreNames = movie.genre_ids.mapNotNull { genreMap[it] }.joinToString(", ")
                genreView.text = genreNames.ifEmpty { "Unknown" }

                Glide.with(movieView.context)
                    .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                    .into(posterView)
                movieView.setOnClickListener {
                    val intent = Intent(movieView.context, DetailsActivity::class.java)
                    intent.putExtra("movieId", movie.id) // Pass the movie ID
                    movieView.context.startActivity(intent)
                }
            } else {
                movieView.visibility = View.INVISIBLE
            }
        }
    }

    override fun getItemCount(): Int = (movies.size + 2) / 3 // Ceiling division for rows

    class MovieRowViewHolder(view: LinearLayout) : RecyclerView.ViewHolder(view) {
        val movieViews: List<View> = (0 until 3).map { view.getChildAt(it) }
    }
}
