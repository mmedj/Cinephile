package com.example.cinephile

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class DetailsActivity : AppCompatActivity() {
    private var isBookmarked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val bookmarkIcon: ImageView = findViewById(R.id.bookmarkIcon)

        bookmarkIcon.setOnClickListener {
            // Toggle bookmark state
            isBookmarked = !isBookmarked
            if (isBookmarked) {
                bookmarkIcon.setImageResource(R.drawable.remove_watchlist)  // Change to filled star
            } else {
                bookmarkIcon.setImageResource(R.drawable.add_watchlist)  // Change to empty star
            }
        }
        // Get the movie ID passed from the previous activity
        val movieId = intent.getIntExtra("movieId", -1)

        if (movieId == -1) {
            Toast.makeText(this, "Movie ID is missing!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set up Retrofit for API calls
        val apiService = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Fetch movie details
        apiService.getMovieDetails(movieId, "4ac21fafbee078016cf47367c9a93b69").enqueue(object : Callback<MovieDetailsResponse> {
            override fun onResponse(call: Call<MovieDetailsResponse>, response: Response<MovieDetailsResponse>) {
                if (response.isSuccessful) {
                    val movieDetails = response.body()
                    if (movieDetails != null) {
                        // Show the movie details on the screen
                        displayMovieDetails(movieDetails)
                    }
                } else {
                    Toast.makeText(this@DetailsActivity, "Failed to fetch movie details.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieDetailsResponse>, t: Throwable) {
                Toast.makeText(this@DetailsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayMovieDetails(details: MovieDetailsResponse) {
        // Set text and images in the UI
        findViewById<TextView>(R.id.movieTitle).text = details.title
        findViewById<TextView>(R.id.movieTagline).text = details.tagline ?: ""
        findViewById<TextView>(R.id.movieOverview).text = details.overview
        findViewById<TextView>(R.id.movieRuntime).text = "${details.runtime ?: "N/A"} mins"
        findViewById<TextView>(R.id.movieReleaseDate).text = "Release Date: ${details.release_date}"
        findViewById<TextView>(R.id.movieVoteAverage).text = "Rating: ${details.vote_average} (${details.vote_count} votes)"
        findViewById<TextView>(R.id.movieBudget).text = "Budget: $${details.budget ?: "N/A"}"
        findViewById<TextView>(R.id.movieRevenue).text = "Revenue: $${details.revenue ?: "N/A"}"
        val genreContainer = findViewById<LinearLayout>(R.id.genreContainer)

        // Remove all previous genre TextViews if any
        genreContainer.removeAllViews()

        // Add a TextView for each genre
        details.genres.forEach { genre ->
            val genreTextView = TextView(this).apply {
                text = genre.name
                setBackgroundResource(R.drawable.genre_chip_background) // Set a custom background drawable
                setPadding(20, 8, 20, 8) // Add some padding
                setTextColor(Color.parseColor("#88A4E8"))
                textSize = 14f // Text size
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 12 // Add space between chips
                }
            }
            genreContainer.addView(genreTextView)
        }
        // Load images using Glide
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${details.poster_path}")
            .into(findViewById<ImageView>(R.id.moviePoster))

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${details.backdrop_path}")
            .into(findViewById<ImageView>(R.id.movieBackdrop))
    }
}
