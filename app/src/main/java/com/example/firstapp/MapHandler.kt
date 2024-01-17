package com.example.firstapp

import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.LocalDateTime
import kotlin.math.abs

class MapHandler(private val mapFragment: SupportMapFragment) {
    private lateinit var googleMap: GoogleMap
    private val markers: MutableList<Marker> = ArrayList()
    private val polylines: MutableList<Polyline> = ArrayList()
    private var startTime: LocalTime = LocalTime.now()
    private var zoomLevel : Float = 17.5F
    private var markerId : Int = 1
    private var laps : Int = 0


    public fun setMarker(latitude: Double, longitude: Double) : Boolean {
        val location = LatLng(latitude, longitude)
        val markerExists = markers.any { it.position == location }
        if(!markerExists && markers.size < 2) {
            mapFragment.getMapAsync(OnMapReadyCallback {
                googleMap = it
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
                val markerOptions =
                    MarkerOptions()
                        .position(location)
                        .title("Measure: " + this.markerId.toString())
                        .snippet("Time: ${LocalDateTime.now()}, Laps: ${this.laps}, Distance: ${calculateTotalDistance()} meters")
                val marker = googleMap.addMarker(markerOptions)
                if (marker != null) {
                    markers.add(marker)
                    this.markerId++
                }
                if (markers.size > 1) {
                    drawRoute()
                }
            })
            return true
        }
        else if(!markerExists  && abs(markers.last().position.latitude - latitude) > 0.00005 &&
            abs(markers.last().position.longitude - longitude) > 0.00005)
        {
            mapFragment.getMapAsync(OnMapReadyCallback {
                googleMap = it
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
                val markerOptions =
                    MarkerOptions()
                        .position(location)
                        .title("Measure: " + this.markerId.toString())
                        .snippet("Time: ${LocalDateTime.now()}, Laps: ${this.laps}, Distance: ${calculateTotalDistance()} meters")
                val marker = googleMap.addMarker(markerOptions)
                if (marker != null) {
                    markers.add(marker)
                    this.markerId++
                }
                if (markers.size > 1) {
                    drawRoute()
                }
                if (abs(markers.first().position.latitude - markers.last().position.latitude) < 0.0001 && abs(markers.first().position.longitude - markers.last().position.longitude) < 0.0001)
                {
                    this.laps++
                }
            })
            return true
        }
        else
            return false
    }

    private fun drawRoute() {
        val lastMarker = markers[markers.size - 2]
        val currentMarker = markers[markers.size - 1]
        val polylineOptions = PolylineOptions()
            .add(lastMarker.position, currentMarker.position)
            .color(0xFF0000FF.toInt()) // Kolor linii

        val polyline = googleMap.addPolyline(polylineOptions)
        polylines.add(polyline)
    }

    private fun calculateDistanceBetweenMarkers(marker1: Marker, marker2: Marker): Float {
        val location1 = marker1.position
        val location2 = marker2.position

        val results = FloatArray(1)
        Location.distanceBetween(
            location1.latitude, location1.longitude,
            location2.latitude, location2.longitude,
            results
        )

        return results[0]
    }

    public fun calculateTotalDistance(): Float {
        var totalDistance = 0f

        if (markers.size < 2) {
            return 0f  // Jeśli jest mniej niż 2 markery, zwracamy odległość równą zero.
        }

        for (i in 0 until markers.size - 1) {
            val marker1 = markers[i]
            val marker2 = markers[i + 1]

            val distance = calculateDistanceBetweenMarkers(marker1, marker2)
            totalDistance += distance
        }

        return totalDistance
    }

    public fun getStartTime() : LocalTime
    {
        return this.startTime
    }

    public fun setStartTime(time: LocalTime)
    {
        this.startTime = time
    }

    public fun getMarkersCount() : Int
    {
        return markers.size
    }

    public fun getMarkersArray() : MutableList<Marker>
    {
        return this.markers
    }

    public fun clearMapData()
    {
        this.markers.clear()
        this.polylines.clear()
        if(::googleMap.isInitialized)
            this.googleMap.clear()
        this.markerId = 1
        this.laps = 0
    }

    public fun getLaps() : Int
    {
        return this.laps
    }

}