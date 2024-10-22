package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.ItunesRequest
import com.example.playlistmaker.data.dto.ItunesResponse
import com.example.playlistmaker.data.dto.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient: NetworkClient {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService: ItunesAPI = retrofit.create(ItunesAPI::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is ItunesRequest) {
            try {
                val response = itunesService.getTracks(dto.expression).execute()
                val body = response.body() ?: Response()
                return body.apply { resultCode = response.code() }
            }
            catch (e: IOException){
                return Response().apply { resultCode = 400 }
            }
        }
        else
            return Response().apply { resultCode = 204 }
    }
}