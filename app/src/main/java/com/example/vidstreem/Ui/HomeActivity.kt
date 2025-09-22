package com.example.vidstreem.Ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.*
import com.example.vidstreem.Data.Api.RetrofitInstance
import com.example.vidstreem.Data.Model.Movie
import com.example.vidstreem.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.trendingMoviesRecyclerView )
        val adapter = MovieAdapter(mutableListOf()) { movie ->
            // Handle movie click
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        RetrofitInstance.api.getMovies().enqueue(object : Callback<List<Movie>> {
            override fun onResponse(
                call: Call<List<Movie>?>,
                response: Response<List<Movie>?>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { movies ->
                        adapter.updateMovies(movies)
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "Failed to fetch movies: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(
                call: Call<List<Movie>?>,
                t: Throwable
            ) {
                Toast.makeText(this@HomeActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}