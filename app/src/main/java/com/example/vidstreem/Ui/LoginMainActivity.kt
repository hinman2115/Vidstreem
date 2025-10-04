package com.example.vidstreem.Ui

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vidstreem.Data.Api.RetrofitInstance
import com.example.vidstreem.Data.Model.LoginRequest
import com.example.vidstreem.Data.Model.LoginResponse
import com.example.vidstreem.R
import com.example.vidstreem.viewmodels.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginMainActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sessionManager: SessionManager


    private val googleSigninLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        result ->
        if(result.resultCode == RESULT_OK){
            val  data = result.data
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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_main)

        // ✅ Initialize SessionManager
        sessionManager = SessionManager(this)
        authViewModel = AuthViewModel(sessionManager)

        // ✅ Setup Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("489097530473-anhlve78oi3oso85l2nvg4h0hmpliet1.apps.googleusercontent.com")
            .requestEmail()
            .build()
        Log.d("token",gso.toString())
        googleSignInClient = GoogleSignIn.getClient(this, gso)

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

        // ✅ Find views
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<LinearLayout>(R.id.loginButton)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val googleSignInButton = findViewById<Button>(R.id.googleSignInButton) // Make sure you have this in XML

        // ✅ Email/Password Login
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
                        sessionManager.saveAuthToken(loginResponse.token)
                        Toast.makeText(this@LoginMainActivity, "Login Successful!", Toast.LENGTH_SHORT).show()

                        goToHome()
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Login failed. Please try again."
                        Log.d("error is here =>", errorMsg)
                        Toast.makeText(this@LoginMainActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@LoginMainActivity, "An error occurred: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        // ✅ Google Login Button
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
        return sessionManager.fetchAuthToken() != null
    }
}
