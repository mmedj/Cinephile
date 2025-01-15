package com.example.cinephile

import android.widget.ListView
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.cinephile.Retrofit.RetrofitInstance
import com.example.cinephile.dataClass.Movie
import com.example.cinephile.dataClass.MovieResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryMoviesActivity : AppCompatActivity() {

    private val apiKey = "4ac21fafbee078016cf47367c9a93b69"
    private lateinit var moviesListView: ListView
    private lateinit var categtext: TextView // Update to lateinit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_movies)
        categtext = findViewById(R.id.categ)
        moviesListView = findViewById(R.id.moviesListView)
        moviesListView = findViewById(R.id.moviesListView)

        val category = intent.getStringExtra("category") ?: "popular"
        categtext.text=category
        // Fetch movies based on category
        fetchMovies(category)
    }

    private fun fetchMovies(category: String) {
        val call = when (category) {
            "popular" -> RetrofitInstance.apiService.getPopularMovies(apiKey)
            "top_rated" -> RetrofitInstance.apiService.getTopRatedMovies(apiKey)
            "upcoming" -> RetrofitInstance.apiService.getUpcomingMovies(apiKey)
            "now_playing" -> RetrofitInstance.apiService.getNowPlayingMovies(apiKey)
            else -> return
        }

        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results ?: emptyList()
                    displayMovies(movies)
                } else {
                    Toast.makeText(this@CategoryMoviesActivity, "Failed to load movies", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Toast.makeText(this@CategoryMoviesActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayMovies(movies: List<Movie>) {
        val adapter = MoviesAdapter(this, movies)
        moviesListView.adapter = adapter
    }
}
class MoviesAdapter(private val context: Context, private val movies: List<Movie>) : BaseAdapter() {

    override fun getCount(): Int = movies.size

    override fun getItem(position: Int): Any = movies[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false)
        val movie = movies[position]

        val posterImageView: ImageView = view.findViewById(R.id.moviePoster)
        val titleTextView: TextView = view.findViewById(R.id.movieTitle)
        val overviewTextView: TextView = view.findViewById(R.id.movieOverview)
        val releaseDateTextView: TextView = view.findViewById(R.id.movieReleaseDate)

        // Load poster image with rounded corners
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
            .placeholder(R.drawable.image_background)
            .apply(RequestOptions().transform(RoundedCorners(24))) // Applying rounded corners transformation

            .into(posterImageView)

        // Set movie details
        titleTextView.text = movie.title
        overviewTextView.text = movie.overview
        releaseDateTextView.text = "Release Date: ${movie.release_date}"
        view.setOnClickListener {
            val intent = Intent(view.context, DetailsActivity::class.java)
            intent.putExtra("movieId", movie.id) // Pass the movie ID
            view.context.startActivity(intent)
        }

        return view
    }
}
