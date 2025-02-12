package com.example.playlistmaker.data.internal_storage

import android.net.Uri

interface InternalStorageManager {
    fun saveImageToInternalStorage(imageUri: Uri): Uri
}