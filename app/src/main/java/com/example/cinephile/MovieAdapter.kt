package com.example.cinephile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cinephile.dataClass.Movie

class MovieAdapter(
    private var movies: List<Movie>,
    private var genreMap: Map<Int, String>,
    private val isSearchView: Boolean = false // Add this parameter to determine which layout to use
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    fun updateMovies(newMovies: List<Movie>) {
        this.movies = newMovies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val layoutId = if (isSearchView) R.layout.movie_search else R.layout.item_movie
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = movies.size

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val posterView: ImageView = itemView.findViewById(R.id.moviePoster2)
        private val titleView: TextView = itemView.findViewById(R.id.movieTitle)
        private val overviewView: TextView = itemView.findViewById(R.id.movieOverview)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.movieRatingBar)
        private val genreView: TextView = itemView.findViewById(R.id.movieGenres)

        fun bind(movie: Movie) {
            titleView.text = movie.title
            overviewView.text = movie.overview
            ratingBar.rating = (movie.vote_average / 2).toFloat()

            // Map genres from genre IDs
            val genreNames = movie.genre_ids
                .mapNotNull { genreId -> this@MovieAdapter.genreMap[genreId] }
                .joinToString(", ")

            genreView.text = if (genreNames.isNotEmpty()) {
                genreNames
            } else {
                "Genre information unavailable"
            }
            // Load poster image
            if (!movie.poster_path.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                    .into(posterView)
            }

            // Set click listener for the entire card
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailsActivity::class.java)
                intent.putExtra("movieId", movie.id)
                itemView.context.startActivity(intent)
            }
        }
    }
    fun updateGenreMap(newGenreMap: Map<Int, String>) {
        genreMap = newGenreMap
        notifyDataSetChanged()
    }
}