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

fun Int.getWordMinuteInCorrectView(): String {
    if ((this >= 10) and (this <= 20)) {
        return "минут"
    }

    val lastDigit = this%10
    return when(lastDigit){
        0,5,6,7,8,9 -> "минут"
        1 -> "минута"
        2,3,4 -> "минуты"
        else -> ""
    }
}

fun String.convertToMilliseconds(): Long{
    val parts = this.split(":")
    if (parts.count() != 2){
        throw Exception("Incorrect format!")
    }
    val mins = parts[0].tryToLong()
    if (mins == (-1).toLong()) {
        throw Exception("Incorrect format!")
    }
    val seconds = parts[1].tryToLong()
    if (seconds == (-1).toLong()) {
        throw Exception("Incorrect format!")
    }
    return (mins * 60 + seconds) * 1000
}