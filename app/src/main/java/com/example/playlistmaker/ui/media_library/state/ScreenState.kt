package com.example.playlistmaker.ui.media_library.state

sealed class ScreenState<T> {
    class Loading<T>: ScreenState<T>()
    data class Empty<T>(val message: String): ScreenState<T>()
    data class Content<T>(val list: List<T>): ScreenState<T>()
}