package com.example.vidstreem.Data.Model

import android.R
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
    val videoUrl: String,
    val duration: Int,
    val category: CategoryDto? = null,
    val categoryName: String? = null,
    val uploadDate: String? = null,
    val isPremium: Boolean? = true,
)
data class Section(
    val title: String,
    val items: List<Movie>
)


data class CategoryDto(
    val categoryId: Int?,
    val name: String?,
    val videos: List<Any>? = emptyList()
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

data class WatchHistoryDto(
    val userId: Int,
    val videoId: Int,
    val lastPosition: Long,
    val duration: Long,
    val deviceType: String = "Android"
)
data class WatchHistoryResponse(
    val lastPosition: Long,
    val duration: Long,
    val percentageWatched: Double,
    val isCompleted: Boolean,
    val deviceType: String,
    val lastWatchedTime: String
)
data class WatchHistory(
    val id: Int,
    val userId: Int,
    val videoId: Int,
    val lastPosition: Long,
    val duration: Long,
    val isCompleted: Boolean
)
data class WatchHistoryItem(
    val videoId: Int,
    val lastPosition: Long,
    val duration: Long,
    val percentageWatched: Double,
    val isCompleted: Boolean,
    val lastWatchedTime: String
)