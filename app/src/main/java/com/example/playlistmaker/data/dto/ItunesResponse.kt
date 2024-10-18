package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.model.Track

class ItunesResponse(val resultCount: Int, val results: List<TrackDto>): Response()