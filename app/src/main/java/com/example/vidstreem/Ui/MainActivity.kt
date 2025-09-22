package com.example.vidstreem.Ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vidstreem.Data.Api.RetrofitInstance
import com.example.vidstreem.Data.Model.LoginResponse
import com.example.vidstreem.Data.Model.RegisterRequest
import com.example.vidstreem.R
import com.example.vidstreem.Util.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val signupbutton = findViewById<LinearLayout>(R.id.signUpButton)
        val Infocard = findViewById<LinearLayout>(R.id.Infocard)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar) // Add this to your XML


        signupbutton.setOnClickListener {

            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request = RegisterRequest(name, email, password, phone,"user")
            RetrofitInstance.api.register(request).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    // Hide the progress bar once the response is received
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        // Handle success
                        Toast.makeText(this@MainActivity, "Registration Successful! Please log in.", Toast.LENGTH_LONG).show()

                        // Navigate to the Login screen
                        val intent = Intent(this@MainActivity, LoginMainActivity::class.java)
                        startActivity(intent)
                        finish() // Close the registration activity
                    } else {
                        // Handle API error (e.g., user already exists)
                        val errorMsg = response.errorBody()?.string() ?: "Registration failed."
                        Toast.makeText(this@MainActivity, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    // Hide the progress bar on failure
                    progressBar.visibility = View.GONE

                    // Handle network failure (e.g., no internet connection)
                    Toast.makeText(this@MainActivity, "An error occurred: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })

        }
    }

}