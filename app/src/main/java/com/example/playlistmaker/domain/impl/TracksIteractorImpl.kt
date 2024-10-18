package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.model.Track
import kotlin.concurrent.thread

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        thread {
            consumer.consume(repository.searchTracks(expression))
        }
    }

    override fun saveTracksToLocalStorage(tracks: ArrayList<Track>) {
        repository.saveTracksToLocalStorage(tracks)
    }


    override fun getTracksFromLocalStorage(consumer: TracksInteractor.TracksConsumer) {
        consumer.consume(repository.getTracksFromLocalStorage())
    }
}