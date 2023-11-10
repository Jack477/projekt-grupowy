package com.example.firstapp

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.time.LocalTime

class MapHandler(private val mapFragment: SupportMapFragment) {
    private lateinit var googleMap: GoogleMap
    private val markers: MutableList<Marker> = ArrayList()
    private val polylines: MutableList<Polyline> = ArrayList()
    private var startTime: LocalTime = LocalTime.now()


    public fun setMarker(latitude: Double, longitude: Double) : Boolean {
        val location = LatLng(latitude, longitude)
        val markerExists = markers.any { it.position == location }
        if(!markerExists) {
            mapFragment.getMapAsync(OnMapReadyCallback {
                googleMap = it
                val marker =
                    googleMap.addMarker(MarkerOptions().position(location).title("My Location"))
                if (marker != null) {
                    markers.add(marker)
                }
                if (markers.size > 1) {
                    drawRoute()
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

}