package com.example.playlistmaker.utils

const val SEARCH_HISTORY = "SEARCH_HISTORY"

const val SONG_MODEL = "SONG"

/**
 * @return -1 - если значение не было преобразовано, во всех остальных случаях возвращает значение в формате Long.
 */
fun String.tryToLong(): Long{
    return try {
        this.toLong()
    }
    catch (_: NumberFormatException){
        -1
    }
}