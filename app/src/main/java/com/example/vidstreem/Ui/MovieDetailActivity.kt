package com.example.vidstreem.Ui

import MovieDetailViewModel
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.vidstreem.Data.Model.Movie
import com.example.vidstreem.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class MovieDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: MovieDetailViewModel
    private var exoPlayer: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var progressBar: ProgressBar
    private lateinit var detailsContainer: View
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var loadingContainer: View // ADD THIS


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movie_detail)

        // Initialize Views
        playerView = findViewById(R.id.player_view)
        progressBar = findViewById(R.id.progress_bar)
        detailsContainer = findViewById(R.id.details_container)
        titleTextView = findViewById(R.id.movie_title_detail)
        descriptionTextView = findViewById(R.id.movie_description_detail)
        loadingContainer = findViewById(R.id.loading_container)

        val playPauseButton: ImageButton? = playerView.findViewById(R.id.exo_play_pause)

        // Set a manual click listener to control the player
        playPauseButton?.setOnClickListener {
            if (exoPlayer?.isPlaying == true) {
                exoPlayer?.pause()
            } else {
                exoPlayer?.play()
            }
        }


        val movieId = intent.getIntExtra("Id", -1)
        if (movieId == -1) {
            Toast.makeText(this, "Error: Movie ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[MovieDetailViewModel::class.java]
        viewModel.fetchMovieDetails(movieId)

        // Observe LiveData for changes
        viewModel.movieDetails.observe(this) { movie ->
            progressBar.visibility = View.GONE
            loadingContainer.visibility = View.GONE

            if (movie != null) {
                bindMovieDetails(movie)
                // CORRECTED: Initialize the player here, now that we have the URL
                initializePlayer(movie.videoUrl)
                detailsContainer.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Failed to load movie details", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun bindMovieDetails(movie: Movie) {
        titleTextView.text = movie.title
        descriptionTextView.text = movie.description
    }

    // CORRECTED: This function now receives the URL directly
    private fun initializePlayer(videoUrl: String?) {
        if (videoUrl.isNullOrEmpty()) return

        // Release any old player instance before creating a new one
        releasePlayer()

        exoPlayer = ExoPlayer.Builder(this).build()
        playerView.player = exoPlayer

        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    // --- CORRECT LIFECYCLE HANDLING ---

    // When the app is no longer visible, release the player.
    public override fun onStop() {
        super.onStop()
        releasePlayer()
    }
}