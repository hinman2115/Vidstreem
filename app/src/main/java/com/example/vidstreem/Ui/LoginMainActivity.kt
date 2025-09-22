package com.example.vidstreem.Ui

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vidstreem.Data.Api.RetrofitInstance
import com.example.vidstreem.Data.Model.LoginRequest
import com.example.vidstreem.Data.Model.LoginResponse
import com.example.vidstreem.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginMainActivity : AppCompatActivity() {

    // You will need a SessionManager instance to save the token.
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_main)
        if (isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SessionManager
        sessionManager = SessionManager(applicationContext)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<LinearLayout>(R.id.loginButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        // val signupButton = findViewById<TextView>(R.id.loginText) // Use if needed for navigation

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show progress bar during the network call
            progressBar.visibility = View.VISIBLE

            val request = LoginRequest(email, password)
            RetrofitInstance.api.login(request).enqueue(object : Callback<LoginResponse> {

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    progressBar.visibility = View.GONE // Hide progress bar

                    if (response.isSuccessful && response.body() != null) {
                        // Login Success
                        val loginResponse = response.body()!!
                        sessionManager.saveAuthToken(loginResponse.token)

                        Toast.makeText(this@LoginMainActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        // Navigate to HomeActivity
                        val intent = Intent(this@LoginMainActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    } else {
                        // Handle login error (e.g., wrong credentials)
                        val errorMsg = response.errorBody()?.string() ?: "Login failed. Please try again."
                        Toast.makeText(this@LoginMainActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@LoginMainActivity, "An error occurred: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun isLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPref.getString("auth_token", null) != null
    }
}
