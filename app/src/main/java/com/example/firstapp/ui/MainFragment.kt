package com.example.firstapp.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.firstapp.LocationHelper
import com.example.firstapp.MapHandler
import com.example.firstapp.FileGPXIO
import com.example.firstapp.RunSession
import com.example.firstapp.R
import com.google.android.gms.maps.SupportMapFragment
import java.time.Duration
import java.time.LocalTime
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat

class MainFragment : Fragment() {

    private lateinit var tvCurrentPos: TextView
    private lateinit var tvLastPos: TextView
    private lateinit var totalDistant: TextView
    private lateinit var totalTime: TextView
    private lateinit var laps: TextView
    private lateinit var pointsNum: TextView
    private lateinit var voiceSwitch: Switch
    private lateinit var locationHelper: LocationHelper
    private lateinit var mapHandler: MapHandler
    private lateinit var mapFragment : SupportMapFragment
    private var sessions: MutableList<RunSession> = ArrayList()
    private var fileGpxIO = FileGPXIO()
    private var sessionStarted : Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private val delayMillis = 350
    private var sessionId = 0

    companion object {
        private const val MY_PERMISSIONS_REQUEST_STORAGE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        tvCurrentPos = root.findViewById(R.id.currentPos)
        tvLastPos = root.findViewById(R.id.lastPoint)
        totalDistant = root.findViewById(R.id.totalDistance)
        totalTime = root.findViewById(R.id.totalTime)
        laps = root.findViewById(R.id.okrazenia)
        pointsNum = root.findViewById(R.id.pointsNum)
        voiceSwitch = root.findViewById(R.id.voiceSwitch)
        locationHelper = LocationHelper(requireContext(), requireActivity())
        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapHandler = MapHandler(mapFragment)

        val btnTraceControl = root.findViewById<Button>(R.id.traceControlButton)
        btnTraceControl.setOnClickListener {
            if (!sessionStarted) {
                sessions.add(RunSession(LocalTime.now()))
                sessionStarted = true

                if(sessionId > 0)
                    mapHandler.clearMapData()

                btnTraceControl.text = "Stop"
                mapHandler.setStartTime(LocalTime.now())
                getLocationTask.run()
            } else {
                sessions[sessionId].stopSession(LocalTime.now(), mapHandler.calculateTotalDistance(), mapHandler.getMarkersArray(), 0)
                sessionStarted = false
                btnTraceControl.text = "Start"
                handler.removeCallbacksAndMessages(null)
                this.sessionId++

                if (checkPermissions()) {
                    fileGpxIO.downloadGpxFile(requireContext(), sessions[sessionId - 1])
                } else {
                    requestPermissions()
                }
            }
        }

        return root
    }

    private fun checkPermissions(): Boolean {
        val writePermission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)

        return writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        ActivityCompat.requestPermissions(requireActivity(), permissions, MY_PERMISSIONS_REQUEST_STORAGE)
    }

    private val getLocationTask = object : Runnable {
        override fun run() {
            if (sessionStarted) {
                tvLastPos.text = "" + locationHelper.getLatitude() + " " + locationHelper.getLongitude()
                locationHelper.getCurrentLocation(tvCurrentPos)
                mapHandler.setMarker(locationHelper.getLatitude(), locationHelper.getLongitude())
                totalDistant.text = "" + mapHandler.calculateTotalDistance().toString() + " m"
                laps.text = mapHandler.getLaps().toString()
                val duration = Duration.between(mapHandler.getStartTime(), LocalTime.now())
                totalTime.text = "" + duration.toHours() + " h " + duration.toMinutes() % 60 + " m " + duration.seconds % 60 + " s"
                pointsNum.text = "" + mapHandler.getMarkersCount()
                handler.postDelayed(this, delayMillis.toLong())
            }
        }
    }
}
