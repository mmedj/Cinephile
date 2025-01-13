    package com.example.cinephile

    import android.content.Intent
    import android.os.Bundle
    import android.widget.Button
    import android.widget.ImageView
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.bumptech.glide.Glide
    import com.example.cinephile.Retrofit.RetrofitInstance
    import com.example.cinephile.dataClass.GenreResponse
    import com.example.cinephile.dataClass.Movie
    import com.example.cinephile.dataClass.MovieResponse
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response

    class HomeActivity : AppCompatActivity() {
        private val apiKey = "4ac21fafbee078016cf47367c9a93b69"
        private lateinit var genreMap: Map<Int, String>
        private val categories = listOf("popular", "top_rated", "upcoming", "now_playing")

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_home)
            findViewById<TextView>(R.id.morePopular).setOnClickListener {
                navigateToCategory("popular")
            }

            findViewById<TextView>(R.id.moreTopRated).setOnClickListener {
                navigateToCategory("top_rated")
            }

            findViewById<TextView>(R.id.moreUpcoming).setOnClickListener {
                navigateToCategory("upcoming")
            }
            findViewById<TextView>(R.id.moreNowPlaying).setOnClickListener {
                navigateToCategory("now_playing")
            }

            val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
            bottomNavigationView.selectedItemId = R.id.nav_home
            bottomNavigationView.setOnItemSelectedListener { navigateBottomNav(it.itemId) }
            val recyclerViews = listOf(
                findViewById<RecyclerView>(R.id.recyclerViewPopular),
                findViewById<RecyclerView>(R.id.recyclerViewTopRated),
                findViewById<RecyclerView>(R.id.recyclerViewUpcoming),
                findViewById<RecyclerView>(R.id.recyclerViewAnother)
            )
            recyclerViews.forEach { setupRecyclerView(it) }
            fetchGenresAndMovies(recyclerViews)
        }
        private fun navigateToCategory(category: String) {
            val intent = Intent(this, CategoryMoviesActivity::class.java)
            intent.putExtra("category", category)
            startActivity(intent)
        }
        private fun navigateBottomNav(itemId: Int): Boolean {
            return when (itemId) {
                R.id.nav_home -> true // Stay on HomeActivity
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
        private fun setupRecyclerView(recyclerView: RecyclerView) {
            recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.setPadding(16, 0, 16, 0)
        }
        private fun fetchGenresAndMovies(recyclerViews: List<RecyclerView>) {
            // Fetch genres first
            RetrofitInstance.apiService.getGenres(apiKey).enqueue(object : Callback<GenreResponse> {
                override fun onResponse(call: Call<GenreResponse>, response: Response<GenreResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        genreMap = response.body()!!.genres.associate { it.id to it.name }
                        fetchAllMovies(recyclerViews)
                    }
                }

                override fun onFailure(call: Call<GenreResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }

        private fun fetchAllMovies(recyclerViews: List<RecyclerView>) {
            categories.forEachIndexed { index, category ->
                fetchMovies(category) { movies ->
                    recyclerViews[index].adapter = MovieAdapter(movies, genreMap)
                }
            }
            // Fetch and display a random popular movie at the top
            fetchMovies("popular") { movies ->
                if (movies.isNotEmpty()) displayTopMovie(movies.random())
            }
        }

        private fun fetchMovies(category: String, onMoviesFetched: (List<Movie>) -> Unit) {
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
                        onMoviesFetched(response.body()!!.results)
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
            val btnGoToMovies = findViewById<Button>(R.id.buttonDetails) // The button to navigate
            topMovieTitle.text = movie.title
            topMovieGenres.text = movie.genre_ids.mapNotNull { genreMap[it] }.joinToString(", ")
                .ifEmpty { "Unknown" }

            Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                .into(topMovieImage)
            btnGoToMovies.setOnClickListener {
                val intent = Intent(this, DetailsActivity::class.java)
                intent.putExtra("movieId", movie.id) // Pass the movie ID
                startActivity(intent)
            }
        }
    }
