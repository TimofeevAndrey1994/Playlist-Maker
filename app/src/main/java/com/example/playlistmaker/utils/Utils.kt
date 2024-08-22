package com.example.playlistmaker.utils

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