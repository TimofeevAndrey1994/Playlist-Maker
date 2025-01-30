package com.example.playlistmaker.data.db.type_converter

import androidx.room.TypeConverter
import java.util.Date

class TypeConverter {
    @TypeConverter
    fun dateToLong(value: Date): Long {
        return value.time
    }

    @TypeConverter
    fun longToDate(value: Long): Date {
        return Date(value)
    }
}