package com.example.playlistmaker.utils
/**
 * @return -1 - если значение не было преобразовано, во всех остальных случаях возвращает значение в формате Long.
 */
fun String.tryToLong(): Long {
    return try {
        this.toLong()
    }
    catch (_: NumberFormatException){
        -1
    }
}

fun Int.getWordTrackInCorrectView(): String {
    val lastDigit = this%10
    return when(lastDigit){
        0,5,6,7,8,9 -> "треков"
        1 -> "трек"
        2,3,4 -> "трека"
        else -> ""
    }
}