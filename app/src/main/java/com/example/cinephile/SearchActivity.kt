package com.example.cinephile;

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set the selected item to Search
        bottomNavigationView.selectedItemId = R.id.nav_search

        // Handle navigation item clicks
        bottomNavigationView.setOnItemSelectedListener { item ->
        when (item.itemId) {
        R.id.nav_home -> {
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(0, 0)
        true
        }
        R.id.nav_search -> true // Stay on the SearchActivity
        R.id.nav_watchlist -> {
        startActivity(Intent(this, WatchlistActivity::class.java))
        overridePendingTransition(0, 0)
        true
        }
        else -> false
        }
        }
        }
        }
