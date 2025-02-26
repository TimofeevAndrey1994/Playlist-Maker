package com.example.playlistmaker.domain.api

interface ExternalNavigator {
    fun shareLink()
    fun openLink()
    fun openEmail()
    fun sharePlaylist(playlistGlobalDescription: String)
}