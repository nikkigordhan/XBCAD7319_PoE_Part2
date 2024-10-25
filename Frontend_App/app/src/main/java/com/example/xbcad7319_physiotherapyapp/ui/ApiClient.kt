package com.example.xbcad7319_physiotherapyapp.ui

import android.content.Context

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {


    private const val BASE_URL = "http://192.168.0.5:5000"

    private const val BASE_URL = "http://10.0.0.38:5000"

    private var retrofit: Retrofit? = null

    fun getRetrofitInstance(context: Context): Retrofit {
        if (retrofit == null) {

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getService(context: Context): ApiService {
        return getRetrofitInstance(context).create(ApiService::class.java)
    }
}




