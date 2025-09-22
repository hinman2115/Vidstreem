package com.example.vidstreem.Data.Model

import android.icu.text.SimpleDateFormat
import java.util.Locale

data class Movie(
    val contentType: String,
    val description: String,
    val fileName: String,
    val hasThumbnail: Boolean,
    val id: Int,
    val size: Int,
    val thumbnailBase64: String,
    val thumbnailContentType: String,
    val title: String,
    val uploadedOn: String
){
    fun getSizeInMB(): String {
        return String.format("%.1f MB", size / (1024.0 * 1024.0))
    }

    fun getFormattedUploadDate(): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(uploadedOn)
            outputFormat.format(date)
        } catch (e: Exception) {
            uploadedOn
        }
    }
}