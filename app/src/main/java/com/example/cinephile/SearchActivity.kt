package com.example.cinephile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinephile.Retrofit.RetrofitInstance
import com.example.cinephile.dataClass.GenreResponse
import com.example.cinephile.dataClass.MovieResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var searchAdapter: MovieAdapter
        private lateinit var searchView: SearchView
        private var genreMap: Map<Int, String> = emptyMap()
        private val yearOptions = listOf("All Years") + (2025 downTo 2001).map { it.toString() } + "2000 or earlier"
        private val ratingOptions = listOf("All Rate", "1 Star", "2 Stars", "3 Stars", "4 Stars", "5 Stars")
        private var currentSearchQuery: String? = null

        private val handler = Handler(Looper.getMainLooper())
        private var searchRunnable: Runnable? = null
        private val SEARCH_DELAY = 500L // 500ms delay

        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_search)

                setupSearchView()
                setupBottomNavigation()
                setupRecyclerView()

                fetchGenres { genres ->
                        genreMap = genres
                        setupGenreSpinner()
                        setupYearSpinner()
                        setupRatingSpinner()
                        fetchFilteredMovies()
                }
        }

        private fun setupSearchView() {
                searchView = findViewById(R.id.search_view)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                                query?.let {
                                        if (it.isNotBlank()) {
                                                currentSearchQuery = it
                                                searchMoviesByTitle(it)
                                        }
                                }
                                searchView.clearFocus() // Hide keyboard
                                return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                                // Remove any pending search requests
                                searchRunnable?.let { handler.removeCallbacks(it) }

                                if (newText.isNullOrEmpty()) {
                                        currentSearchQuery = null
                                        fetchFilteredMovies()
                                        return true
                                }

                                // Create new search request with delay
                                searchRunnable = Runnable {
                                        if (newText.length >= 2) { // Only search if 2 or more characters
                                                currentSearchQuery = newText
                                                searchMoviesByTitle(newText)
                                        }
                                }

                                // Schedule the search after delay
                                handler.postDelayed(searchRunnable!!, SEARCH_DELAY)
                                return true
                        }
                })
        }

        private fun setupBottomNavigation() {
                val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
                bottomNavigationView.selectedItemId = R.id.nav_search

                bottomNavigationView.setOnItemSelectedListener { item ->
                        when (item.itemId) {
                                R.id.nav_home -> {
                                        startActivity(Intent(this, HomeActivity::class.java))
                                        finish()
                                        overridePendingTransition(0, 0)
                                        true
                                }
                                R.id.nav_search -> true
                                R.id.nav_watchlist -> {
                                        startActivity(Intent(this, WatchlistActivity::class.java))
                                        finish()
                                        overridePendingTransition(0, 0)
                                        true
                                }
                                else -> false
                        }
                }
        }

        private fun setupRecyclerView() {
                recyclerView = findViewById(R.id.recycler_view)
                recyclerView.layoutManager = LinearLayoutManager(this)
                searchAdapter = MovieAdapter(emptyList(), genreMap, isSearchView = true)
                recyclerView.adapter = searchAdapter
        }

        private fun setupGenreSpinner() {
                val genreSpinner = findViewById<Spinner>(R.id.spinner_genre)
                val genreNames = listOf("All Genres") + genreMap.values.toList()
                val genreAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genreNames)
                genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                genreSpinner.adapter = genreAdapter

                genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                                if (currentSearchQuery.isNullOrEmpty()) {
                                        fetchFilteredMovies()
                                }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }

        private fun setupYearSpinner() {
                val yearSpinner = findViewById<Spinner>(R.id.spinner_year)
                val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yearOptions)
                yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                yearSpinner.adapter = yearAdapter

                yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                                if (currentSearchQuery.isNullOrEmpty()) {
                                        fetchFilteredMovies()
                                }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }

        private fun setupRatingSpinner() {
                val ratingSpinner = findViewById<Spinner>(R.id.spinner_rating)
                val ratingAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ratingOptions)
                ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                ratingSpinner.adapter = ratingAdapter

                ratingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                                if (currentSearchQuery.isNullOrEmpty()) {
                                        fetchFilteredMovies()
                                }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }

        private fun fetchGenres(callback: (Map<Int, String>) -> Unit) {
                val apiKey = "4ac21fafbee078016cf47367c9a93b69"

                RetrofitInstance.apiService.getGenres(apiKey)
                        .enqueue(object : Callback<GenreResponse> {
                                override fun onResponse(call: Call<GenreResponse>, response: Response<GenreResponse>) {
                                        if (response.isSuccessful) {
                                                val genres = response.body()?.genres ?: emptyList()
                                                val genreMap = genres.associateBy({ it.id }, { it.name })
                                                searchAdapter.updateGenreMap(genreMap)
                                                callback(genreMap)
                                        } else {
                                                Log.e("SearchActivity", "Error fetching genres: ${response.message()}")
                                                Toast.makeText(this@SearchActivity, "Error loading genres", Toast.LENGTH_SHORT).show()
                                        }
                                }

                                override fun onFailure(call: Call<GenreResponse>, t: Throwable) {
                                        Log.e("SearchActivity", "Error: ${t.message}")
                                        Toast.makeText(this@SearchActivity, "Network error", Toast.LENGTH_SHORT).show()
                                }
                        })
        }

        private fun searchMoviesByTitle(query: String) {
                val apiKey = "4ac21fafbee078016cf47367c9a93b69"

                RetrofitInstance.apiService.searchMovies(
                        apiKey = apiKey,
                        query = query.trim()
                ).enqueue(object : Callback<MovieResponse> {
                        override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                                if (response.isSuccessful) {
                                        val movies = response.body()?.results ?: emptyList()
                                        if (movies.isEmpty()) {
                                                Toast.makeText(this@SearchActivity, "No movies found for '$query'", Toast.LENGTH_SHORT).show()
                                        }
                                        searchAdapter.updateMovies(movies)
                                } else {
                                        handleError("Error searching movies: ${response.message()}")
                                }
                        }

                        override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                                handleError("Network error: ${t.localizedMessage}")
                        }
                })
        }

        private fun fetchFilteredMovies() {
                val apiKey = "4ac21fafbee078016cf47367c9a93b69"

                val genreSpinner = findViewById<Spinner>(R.id.spinner_genre)
                val yearSpinner = findViewById<Spinner>(R.id.spinner_year)
                val ratingSpinner = findViewById<Spinner>(R.id.spinner_rating)

                val selectedGenreName = genreSpinner.selectedItem?.toString() ?: "All Genres"
                val selectedGenreId = if (selectedGenreName == "All Genres") null
                else genreMap.entries.find { it.value == selectedGenreName }?.key

                val selectedYear = yearSpinner.selectedItem?.toString()
                val releaseYear = when {
                        selectedYear == "All Years" -> null
                        selectedYear == "2000 or earlier" -> 2000
                        else -> selectedYear?.toIntOrNull()
                }

                val selectedRating = ratingSpinner.selectedItem?.toString()
                val ratingRange = when (selectedRating) {
                        "1 Star" -> 2.0 to 4.0
                        "2 Stars" -> 4.0 to 6.0
                        "3 Stars" -> 6.0 to 8.0
                        "4 Stars" -> 8.0 to 9.0
                        "5 Stars" -> 9.0 to 10.0
                        else -> null
                }

                RetrofitInstance.apiService.discoverMovies(
                        apiKey = apiKey,
                        genreId = selectedGenreId?.toString() ?: "",
                        minRating = ratingRange?.first,
                        maxRating = ratingRange?.second,
                        releaseYear = releaseYear
                ).enqueue(object : Callback<MovieResponse> {
                        override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                                if (response.isSuccessful) {
                                        val movies = response.body()?.results ?: emptyList()
                                        if (movies.isEmpty()) {
                                                Toast.makeText(this@SearchActivity, "No movies found matching your criteria", Toast.LENGTH_SHORT).show()
                                        }
                                        searchAdapter.updateMovies(movies)
                                } else {
                                        handleError("Error fetching movies: ${response.message()}")
                                }
                        }

                        override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                                handleError("Network error: ${t.localizedMessage}")
                        }
                })
        }

        private fun handleError(message: String) {
                Log.e("SearchActivity", message)
                Toast.makeText(this@SearchActivity, message, Toast.LENGTH_SHORT).show()
        }

        override fun onDestroy() {
                super.onDestroy()
                searchRunnable?.let { handler.removeCallbacks(it) }
        }
}