package com.example.cinephile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class WatchlistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var adapter: WatchlistAdapter
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watchlist)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set the selected item to Search
        bottomNavigationView.selectedItemId = R.id.nav_search

        // Handle navigation item clicks
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_watchlist -> true // Stay on the SearchActivity

                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                else -> false
            }
        }
        setupViews()
        setupRecyclerView()
        loadWatchlist()
    }



    private fun setupViews() {
        recyclerView = findViewById(R.id.watchlistRecyclerView)
        emptyStateText = findViewById(R.id.emptyStateText)
    }

    private fun setupRecyclerView() {
        adapter = WatchlistAdapter(
            onItemClick = { movie -> navigateToDetails(movie.movieId) },
            onRemoveClick = { movie -> removeFromWatchlist(movie.movieId) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadWatchlist() {
        auth.currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .collection("watchlist")
                .orderBy("addedAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Handle error, possibly show a message to the user
                        return@addSnapshotListener
                    }

                    val movies = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(WatchlistMovie::class.java)
                    } ?: emptyList()

                    adapter.submitList(movies)
                    showEmptyState(movies.isEmpty())
                }
        }
    }

    private fun removeFromWatchlist(movieId: Int) {
        auth.currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .collection("watchlist")
                .document(movieId.toString())
                .delete()
                .addOnSuccessListener {
                    // Optionally notify the user that the item was removed
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }

    private fun navigateToDetails(movieId: Int) {
        startActivity(Intent(this, DetailsActivity::class.java).apply {
            putExtra("movieId", movieId)
        })
    }

    private fun showEmptyState(show: Boolean) {
        emptyStateText.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }
}

class WatchlistAdapter(
    private val onItemClick: (WatchlistMovie) -> Unit,
    private val onRemoveClick: (WatchlistMovie) -> Unit
) : RecyclerView.Adapter<WatchlistAdapter.MovieViewHolder>() {

    private var movies: List<WatchlistMovie> = emptyList()

    fun submitList(newList: List<WatchlistMovie>) {
        movies = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_watchlist_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val posterImage: ImageView = itemView.findViewById(R.id.moviePoster)
        private val titleText: TextView = itemView.findViewById(R.id.movieTitle)
        private val overviewText: TextView = itemView.findViewById(R.id.movieOverview)
        private val ratingText: TextView = itemView.findViewById(R.id.movieRating)
        private val removeButton: ImageButton = itemView.findViewById(R.id.removeButton)

        fun bind(movie: WatchlistMovie) {
            titleText.text = movie.title
            overviewText.text = movie.overview
            ratingText.text = "★ ${movie.rating}"

            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w200${movie.posterPath}")
                .into(posterImage)

            itemView.setOnClickListener { onItemClick(movie) }
            removeButton.setOnClickListener { onRemoveClick(movie) }
        }
    }
}
