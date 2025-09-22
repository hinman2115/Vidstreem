package com.example.vidstreem.Data.Api

import com.example.vidstreem.Data.Model.LoginRequest
import com.example.vidstreem.Data.Model.LoginResponse
import com.example.vidstreem.Data.Model.Movie
import com.example.vidstreem.Data.Model.RegisterRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("User/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
    @POST("User/register")
    fun register(@Body registerRequest: RegisterRequest): Call<Unit>

    @GET("VideohandelApi/thumbnails")
    fun getMovies(): Call<List<Movie>>
}