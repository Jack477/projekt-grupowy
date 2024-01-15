package com.example.firstapp

import com.google.android.gms.maps.model.Marker
import java.time.Duration
import java.time.LocalTime

class RunSession(startTime: LocalTime) {
    private var laps: Int = 0
    private var totalDistance: Float = 0.0F
    private var markers: MutableList<Marker> = ArrayList()
    private lateinit var startTime: LocalTime
    private lateinit var finishTime: LocalTime
    private lateinit var duration: Duration

    init {
        this.startTime = startTime
    }

    fun stopSession(finishTime: LocalTime, totalDistance: Float, markers: MutableList<Marker>, laps: Int)
    {
        this.laps = laps
        this.totalDistance = totalDistance
        this.markers = markers
        this.finishTime = finishTime
        this.duration = Duration.between(this.startTime, this.finishTime)
    }

    fun getMarkersList(): MutableList<Marker> {
        return markers
    }

}