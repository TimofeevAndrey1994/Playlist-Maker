package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.ItunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesAPI {
    @GET("/search?entity=song")
    suspend fun getTracks(@Query("term") searchText: String): ItunesResponse
}