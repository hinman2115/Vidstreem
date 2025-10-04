package com.example.vidstreem.Data.Model

import com.google.gson.annotations.SerializedName

// Represents a single movie/video item for listing
data class Movie(
    val id: Int,
    val contentType: String,
    val uploadedOn: String,
    val title: String,
    val description: String?,
    val size: Long,
    val hasThumbnail: Boolean?,
    val thumbnailUrl: String?,
    val videoUrl: String
)


data class MovieSection(
    val title: String,
    val movies: List<Movie>?
)


data class SearchResult(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("contentType")
    val contentType: String,

    @SerializedName("uploadedOn")
    val uploadedOn: String,

    @SerializedName("videoUrl")
    val videoUrl: String,

    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String,

    @SerializedName("size")
    val size: Long
)