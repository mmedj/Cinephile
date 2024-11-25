package com.example.cinephile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private val apiKey = "4ac21fafbee078016cf47367c9a93b69"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val recyclerViews = listOf(
            findViewById<RecyclerView>(R.id.recyclerViewPopular),
            findViewById<RecyclerView>(R.id.recyclerViewTopRated),
            findViewById<RecyclerView>(R.id.recyclerViewUpcoming),
            findViewById<RecyclerView>(R.id.recyclerViewAnother)
        )

        val categories = listOf("popular", "top_rated", "upcoming", "now_playing")

        recyclerViews.forEachIndexed { index, recyclerView ->
            setupRecyclerView(recyclerView)
            fetchMovies(categories[index], recyclerView)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            clipToPadding = false
            setPadding(resources.getDimensionPixelSize(R.dimen.recycler_padding), 0,
                resources.getDimensionPixelSize(R.dimen.recycler_padding), 0)
        }
    }

    private fun fetchMovies(category: String, recyclerView: RecyclerView) {
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
                    val adapter = MovieAdapter(movies)
                    recyclerView.adapter = adapter
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}