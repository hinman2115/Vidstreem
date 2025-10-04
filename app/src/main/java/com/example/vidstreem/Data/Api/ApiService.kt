package com.example.vidstreem.Data.Api

import com.example.vidstreem.Data.Model.GoogleLoginRequest
import com.example.vidstreem.Data.Model.LoginRequest
import com.example.vidstreem.Data.Model.LoginResponse
import com.example.vidstreem.Data.Model.Movie
import com.example.vidstreem.Data.Model.RegisterRequest
import com.example.vidstreem.Data.Model.SearchResult
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("User/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
    @POST("User/register")
    fun register(@Body registerRequest: RegisterRequest): Call<Unit>

    @GET("VideohandelApi/thumbnails")
    fun getMovies(): Call<List<Movie>>

    @POST("User/google-login")
    fun googleLogin(@Body request: GoogleLoginRequest): Call<LoginResponse>


    @GET("VideohandelApi/Search")
    fun searchmovie(@Query("name") name: String): Call<List<Movie>>

    @GET("VideohandelApi/{id}") // Example endpoint, use your actual path
    fun getMovieDetails(@Path("id") movieId: Int): Call<Movie>

}