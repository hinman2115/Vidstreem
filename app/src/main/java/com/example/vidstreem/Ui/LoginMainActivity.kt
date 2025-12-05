package com.example.vidstreem.Ui

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.vidstreem.Data.Api.RetrofitInstance
import com.example.vidstreem.Data.Model.LoginRequest
import com.example.vidstreem.Data.Model.LoginResponse
import com.example.vidstreem.R
import com.example.vidstreem.viewmodels.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginMainActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager

    // Keep splash screen visible while checking session
    private var keepSplashOnScreen = true

    private val googleSigninLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    authViewModel.googleLogin(idToken, this)
                    goToHome()
                }
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign-in failed: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE super.onCreate()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        super.onCreate(savedInstanceState)

        // Initialize SessionManager first
        sessionManager = SessionManager(this)
        authViewModel = AuthViewModel(sessionManager)

        // Check if user is already logged in during splash
        checkLoginStatus()

        enableEdgeToEdge()
        setContentView(R.layout.activity_login_main)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup UI elements
        setupLoginUI()
        setubgoogelsing()
    }

    private fun  setubgoogelsing (){
        // Setup Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("489097530473-anhlve78oi3oso85l2nvg4h0hmpliet1.apps.googleusercontent.com")
            .requestEmail()
            .build()
        Log.d("token", gso.toString())
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    private fun checkLoginStatus() {
        lifecycleScope.launch {
            // Simulate initialization/checking auth (minimum 2 seconds for splash visibility)
            delay(1500)

            // Check if user is logged in
            if (isLoggedIn()) {
                Log.d("check this login hited","me bi token got ")
                // User is already logged in, go to home
                startActivity(Intent(this@LoginMainActivity, HomeActivity::class.java))
                finish()
            } else {
                // User not logged in, show login screen
                keepSplashOnScreen = false
            }
        }
    }

    private fun setupLoginUI() {
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<LinearLayout>(R.id.loginButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val googleSignInButton = findViewById<Button>(R.id.googleSignInButton)

        // Email/Password Login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            val request = LoginRequest(email, password)
            RetrofitInstance.api.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!
                        Log.d("JWT",response.toString())
                        sessionManager.saveAuthToken(loginResponse.token)
                        Toast.makeText(
                            this@LoginMainActivity,
                            loginResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        goToHome()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Login failed. Please try again."
                        Log.d("error is here =>", errorMsg)
                        Toast.makeText(this@LoginMainActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@LoginMainActivity,
                        "An error occurred: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        // Google Login Button
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSigninLauncher.launch(signInIntent)
    }

    private fun goToHome() {
        val intent = Intent(this@LoginMainActivity, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun isLoggedIn(): Boolean {
        Log.d("thited token work","me bi token got ")
        return sessionManager.fetchAuthToken() != null
    }
}
