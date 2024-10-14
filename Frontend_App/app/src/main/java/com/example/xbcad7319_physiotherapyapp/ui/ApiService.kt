package com.example.xbcad7319_physiotherapyapp.ui

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/auth/register")
    fun registerUser(@Body user: User): Call<ResponseBody>

    @POST("/api/auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<ResponseBody>
}

