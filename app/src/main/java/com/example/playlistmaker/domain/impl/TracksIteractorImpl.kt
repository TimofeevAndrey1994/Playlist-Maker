package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.model.Track
import kotlin.concurrent.thread

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {
    override fun getTrackFromLocalStorageById(trackId: Long): Track? {
        return repository.getTrackFromLocalStorageById(trackId)
    }

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        thread {
            consumer.consume(repository.searchTracks(expression))
        }
    }

    override fun saveTrackToLocalStorage(track: Track): ArrayList<Track> {
        return repository.saveTrackToLocalStorage(track)
    }

    override fun clearLocalStorage() {
        repository.clearLocalStorage()
    }


    override fun getTracksFromLocalStorage(consumer: TracksInteractor.TracksConsumer) {
        consumer.consume(repository.getTracksFromLocalStorage())
    }
}