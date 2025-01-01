package com.example.cinephile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private val apiKey = "4ac21fafbee078016cf47367c9a93b69"
    private lateinit var genreMap: Map<Int, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home

        // Handle navigation item clicks
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true // Stay on the HomeActivity
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }

                R.id.nav_watchlist -> {
                    startActivity(Intent(this, WatchlistActivity::class.java))
                    true
                }

                else -> false
            }
        }

        val recyclerViews = listOf(
            findViewById<RecyclerView>(R.id.recyclerViewPopular),
            findViewById<RecyclerView>(R.id.recyclerViewTopRated),
            findViewById<RecyclerView>(R.id.recyclerViewUpcoming),
            findViewById<RecyclerView>(R.id.recyclerViewAnother)
        )

        val categories = listOf("popular", "top_rated", "upcoming", "now_playing")

        // Fetch genres first and then fetch movies
        fetchGenres {
            recyclerViews.forEachIndexed { index, recyclerView ->
                setupRecyclerView(recyclerView)
                fetchMovies(categories[index], recyclerView)
            }

            // Display a random popular movie at the top
            fetchMovies("popular", null, isForTopDisplay = true)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            clipToPadding = false
            setPadding(
                resources.getDimensionPixelSize(R.dimen.recycler_padding), 0,
                resources.getDimensionPixelSize(R.dimen.recycler_padding), 0
            )
        }
    }

    private fun fetchGenres(onGenresFetched: () -> Unit) {
        val call = RetrofitInstance.apiService.getGenres(apiKey)
        call.enqueue(object : Callback<GenreResponse> {
            override fun onResponse(call: Call<GenreResponse>, response: Response<GenreResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    genreMap = response.body()!!.genres.associateBy({ it.id }, { it.name })
                    onGenresFetched()
                }
            }

            override fun onFailure(call: Call<GenreResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun fetchMovies(category: String, recyclerView: RecyclerView?, isForTopDisplay: Boolean = false) {
        val call = when (category) {
            "popular" -> RetrofitInstance.apiService.getPopularMovies(apiKey)
            "top_rated" -> RetrofitInstance.apiService.getTopRatedMovies(apiKey)
            "upcoming" -> RetrofitInstance.apiService.getUpcomingMovies(apiKey)
            "now_playing" -> RetrofitInstance.apiService.getNowPlayingMovies(apiKey)
            else -> return
        }

        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val movies = response.body()!!.results

                    if (isForTopDisplay) {
                        // Display a random movie at the top
                        displayTopMovie(movies.random())
                    } else if (recyclerView != null) {
                        val adapter = MovieAdapter(movies, genreMap)
                        recyclerView.adapter = adapter
                    }
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun displayTopMovie(movie: Movie) {
        val topMovieImage = findViewById<ImageView>(R.id.topMovieImage)
        val topMovieTitle = findViewById<TextView>(R.id.topMovieTitle)
        val topMovieGenres = findViewById<TextView>(R.id.topMovieGenres)

        // Set movie details
        topMovieTitle.text = movie.title
        val genreNames = movie.genre_ids.mapNotNull { genreMap[it] }.joinToString(", ")
        topMovieGenres.text = genreNames.ifEmpty { "Unknown" }

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
            .into(topMovieImage)
    }
}
