
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vidstreem.Data.Model.Movie
import com.example.vidstreem.R

class MovieAdapter(private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    private val movies = mutableListOf<Movie>()
    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moviePoster: ImageView = view.findViewById(R.id.movieThumbnail)
        val movieTitle: TextView = view.findViewById(R.id.movieTitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]

        holder.movieTitle.text = movie.title

         Glide.with(holder.itemView.context)
             .asBitmap()
             .load(movie.thumbnailUrl)
             .placeholder(R.drawable.movie_placeholder)
             .into(holder.moviePoster)

        holder.itemView.setOnClickListener { onMovieClick(movie) }
    }

    override fun getItemCount(): Int = movies.size

    fun updateMovies(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }
}
