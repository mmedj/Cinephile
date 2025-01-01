package com.example.cinephile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.loginButton).setOnClickListener { attemptLogin() }
        findViewById<TextView>(R.id.registerTextView).setOnClickListener { navigateToRegister() }
    }

    private fun attemptLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        when {
            email.isEmpty() || password.isEmpty() -> {
                showToast("Please enter both email and password")
                return
            }
            else -> performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                showToast("Login successful!")
                navigateToHome()
            }
            .addOnFailureListener { exception ->
                showToast("Authentication failed: ${exception.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onStart() {
        super.onStart()
        auth.currentUser?.let { navigateToHome() }
    }
}