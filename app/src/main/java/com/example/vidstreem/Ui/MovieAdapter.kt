package com.example.vidstreem.Ui

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.vidstreem.Data.Model.Movie
import com.example.vidstreem.R

class MovieAdapter(
    private val movies: MutableList<Movie> = mutableListOf(),
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = itemView.findViewById(R.id.movieThumbnail)
        val title: TextView = itemView.findViewById(R.id.movieTitle)
        val description: TextView = itemView.findViewById(R.id.movieDescription)
        val size: TextView = itemView.findViewById(R.id.movieSize)
        val loader: ProgressBar = itemView.findViewById(R.id.thumbnailLoader)
        val playOverlay: ImageView = itemView.findViewById(R.id.playOverlay)
        val metadata: LinearLayout = itemView.findViewById(R.id.movieMetadata)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.title.text = movie.title ?: "No Title"

        // Set description if available, handling nullability
        if (!movie.description.isNullOrEmpty()) {
            holder.description.text = movie.description
            holder.description.visibility = View.VISIBLE
        } else {
            holder.description.visibility = View.GONE
        }

        holder.size.text = movie.getSizeInMB()
        // The metadata is hidden by default in XML, but we make it visible here.
        holder.metadata.visibility = View.VISIBLE

        // Load thumbnail
        if (movie.hasThumbnail) {
            loadThumbnailFromBase64(holder, movie.thumbnailBase64)
        } else {
            // Show placeholder if no thumbnail
            holder.thumbnail.setImageResource(R.drawable.movie_placeholder)
            holder.loader.visibility = View.GONE
        }
        // Click listener
        holder.itemView.setOnClickListener {
            onMovieClick(movie)
        }

        // Hover effect for play overlay
        holder.itemView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    holder.playOverlay.visibility = View.VISIBLE
                    holder.itemView.alpha = 0.8f
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    holder.playOverlay.visibility = View.GONE
                    holder.itemView.alpha = 1.0f
                }
            }
            false
        }
    }
    override fun getItemCount(): Int = movies.size

    private fun loadThumbnailFromBase64(holder: MovieViewHolder, base64String: String?) {
        try {
            holder.loader.visibility = View.VISIBLE
            if (base64String.isNullOrEmpty()) {
                holder.thumbnail.setImageResource(R.drawable.movie_placeholder)
                return
            }

            val decodedString = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            if (bitmap != null) {
                holder.thumbnail.setImageBitmap(bitmap)
            } else {
                holder.thumbnail.setImageResource(R.drawable.movie_placeholder)
            }
        } catch (e: Exception) {
            holder.thumbnail.setImageResource(R.drawable.movie_placeholder)
            Log.e("MovieAdapter", "Error loading thumbnail: ${e.message}")
        } finally {
            holder.loader.visibility = View.GONE
        }
    }

    fun updateMovies(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }
}

