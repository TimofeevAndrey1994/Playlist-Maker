package com.example.playlistmaker.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {
    @GET("/search?entity=song")
    fun getSongs(@Query("term") searchText: String): Call<ItunesResponse>
}