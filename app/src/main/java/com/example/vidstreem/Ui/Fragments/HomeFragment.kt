package com.example.vidstreem.Ui.Fragments


import MovieAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vidstreem.Data.Api.RetrofitInstance
import com.example.vidstreem.Data.Model.Movie
import com.example.vidstreem.R
import com.example.vidstreem.Ui.MovieDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.jvm.java

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        loadMovies()

    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.moviesRecyclerView)

        // Only pass click handler (no need to pass movie list now)
        movieAdapter = MovieAdapter { movie ->
            val i = Intent(context, MovieDetailActivity::class.java)
                .putExtra("Id", movie.id)
            startActivity(i)
            Toast.makeText(context, "Clicked: ${movie.title}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = movieAdapter
        recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )
    }


    private fun loadMovies() {
        RetrofitInstance.api.getMovies().enqueue(object : Callback<List<Movie>> {
            override fun onResponse(call: Call<List<Movie>?>, response: Response<List<Movie>?>) {
                if (response.isSuccessful) {
                    response.body()?.let { movies ->

                        Log.d("responce is this =>",response.body().toString())
                        movieAdapter.updateMovies(movies)
                    }
                } else {

                    Toast.makeText(context, "Failed to fetch movies: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Movie>?>, t: Throwable) {
                if (!isAdded) {
                    return
                }
                Toast.makeText(context, "Error at Home fragment: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}