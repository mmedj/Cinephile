package com.example.cinephile

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

data class WatchlistMovie(
    val movieId: Int = 0,
    val title: String = "",
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String = "",
    val rating: Double = 0.0,
    val addedAt: Long = System.currentTimeMillis()
){
    constructor() : this(0, "", null, null, "", 0.0, System.currentTimeMillis())}

class DetailsActivity : AppCompatActivity() {
    private var isBookmarked = false
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var movieId: Int = -1
    private var currentMovie: MovieDetailsResponse? = null
    private lateinit var bookmarkIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        bookmarkIcon = findViewById(R.id.bookmarkIcon)

        // Get movie ID
        movieId = intent.getIntExtra("movieId", -1)
        if (movieId == -1) {
            showToast("Movie ID is missing!")
            finish()
            return
        }

        setupWatchlistButton()
        checkIfMovieInWatchlist()
        fetchMovieDetails()
    }

    private fun setupWatchlistButton() {
        bookmarkIcon.setOnClickListener {
            if (auth.currentUser == null) {
                showToast("Please login to use watchlist")
                return@setOnClickListener
            }
            if (isBookmarked) removeFromWatchlist() else addToWatchlist()
        }
    }

    private fun fetchMovieDetails() {
        val apiService = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        apiService.getMovieDetails(movieId, "4ac21fafbee078016cf47367c9a93b69")
            .enqueue(object : Callback<MovieDetailsResponse> {
                override fun onResponse(
                    call: Call<MovieDetailsResponse>,
                    response: Response<MovieDetailsResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            currentMovie = it
                            displayMovieDetails(it)
                        }
                    } else {
                        showToast("Failed to fetch movie details.")
                    }
                }

                override fun onFailure(call: Call<MovieDetailsResponse>, t: Throwable) {
                    showToast("Error: ${t.message}")
                }
            })
    }

    private fun addToWatchlist() {
        auth.currentUser?.let { user ->
            currentMovie?.let { movie ->
                val watchlistMovie = WatchlistMovie(
                    movieId = movieId,
                    title = movie.title,
                    posterPath = movie.poster_path,
                    backdropPath = movie.backdrop_path,
                    overview = movie.overview,
                    rating = movie.vote_average
                )

                db.collection("users")
                    .document(user.uid)
                    .collection("watchlist")
                    .document(movieId.toString())
                    .set(watchlistMovie)
                    .addOnSuccessListener {
                        isBookmarked = true
                        updateBookmarkIcon()
                        showToast("Added to watchlist")
                    }
                    .addOnFailureListener { e ->
                        showToast("Failed to add: ${e.message}")
                    }
            }
        }
    }

    private fun removeFromWatchlist() {
        auth.currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .collection("watchlist")
                .document(movieId.toString())
                .delete()
                .addOnSuccessListener {
                    isBookmarked = false
                    updateBookmarkIcon()
                    showToast("Removed from watchlist")
                }
                .addOnFailureListener { e ->
                    showToast("Failed to remove: ${e.message}")
                }
        }
    }

    private fun checkIfMovieInWatchlist() {
        auth.currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .collection("watchlist")
                .document(movieId.toString())
                .get()
                .addOnSuccessListener { document ->
                    isBookmarked = document.exists()
                    updateBookmarkIcon()
                }
        }
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

        setupGenres(details)
        loadImages(details)
    }

    private fun setupGenres(details: MovieDetailsResponse) {
        val genreContainer = findViewById<LinearLayout>(R.id.genreContainer)
        genreContainer.removeAllViews()

        details.genres.forEach { genre ->
            val genreTextView = TextView(this).apply {
                text = genre.name
                setBackgroundResource(R.drawable.genre_chip_background)
                setPadding(20, 8, 20, 8)
                setTextColor(Color.parseColor("#88A4E8"))
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 12
                }
            }
            genreContainer.addView(genreTextView)
        }
    }

    private fun loadImages(details: MovieDetailsResponse) {
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${details.poster_path}")
            .into(findViewById<ImageView>(R.id.moviePoster))

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${details.backdrop_path}")
            .into(findViewById<ImageView>(R.id.movieBackdrop))
    }

    private fun updateBookmarkIcon() {
        bookmarkIcon.setImageResource(
            if (isBookmarked) R.drawable.remove_watchlist
            else R.drawable.add_watchlist
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}