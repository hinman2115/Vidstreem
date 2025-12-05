package com.example.vidstreem.Util

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.*
import com.example.vidstreem.Data.Model.*
import com.example.vidstreem.R

class SectionAdapter(
    private val onMovieClick: (Movie) -> Unit
) : RecyclerView.Adapter<SectionAdapter.SectionVH>() {

    private val sections = mutableListOf<Section>()

    class SectionVH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.sectionTitle)
        val list: RecyclerView = view.findViewById(R.id.sectionList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_section, parent, false)
        return SectionVH(v)
    }

    override fun onBindViewHolder(holder: SectionVH, position: Int) {
        val section = sections[position]
        holder.title.text = section.title
        holder.list.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        val movieAdapter = MovieAdapter(onMovieClick)
        holder.list.adapter = movieAdapter
        movieAdapter.updateMovies(section.items)
    }

    override fun getItemCount() = sections.size

    fun submitSections(newSections: List<Section>) {
        sections.clear()
        sections.addAll(newSections)
        notifyDataSetChanged()
    }
}
