package com.example.firstapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.firstapp.LocationHelper
import com.example.firstapp.MapHandler
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var totalDistant: TextView
    private lateinit var totalTime: TextView
    private lateinit var locationHelper: LocationHelper
    private lateinit var mapHandler: MapHandler
    private lateinit var mapFragment : SupportMapFragment

    private var sessionStarted : Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private val delayMillis = 350

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLatitude=findViewById(R.id.Latitude)
        tvLongitude=findViewById(R.id.Longitude)
        totalDistant=findViewById(R.id.totalDistant)
        totalTime=findViewById(R.id.totalTime)
        locationHelper = LocationHelper(this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapHandler = MapHandler(mapFragment)

        val btnClickMe = findViewById<Button>(R.id.mybutton)
        val btnTraceControl = findViewById<Button>(R.id.traceControlButton)
        val tvMyTextView = findViewById<TextView>(R.id.textView)
        var timesClicked = 0
        btnClickMe.text = "Haha" // ustawienie atrybutu tekstu

        // TODO Kod odpowiedzialny za pobieranie lokalizacji wjebać do innego pliku - Done
        // TODO Podpiąć getCurrentLocation() pod button a potem co 0.5sek aktualizować - Done
        // TODO Dodac zliczanie okrazen do MapHandlera
        /* TODO UtworzycKlase RunTrace która będzie reprezentowała dany bieg usera
            - markery z MapHandlera
            - odczyt odlegosci
            - odczyt czasu
            - odczyt pokonanych okrazen
         */
        // TODO przepisac kod na MVVM


        locationHelper.getCurrentLocation(tvLatitude, tvLongitude)

        btnClickMe.setOnClickListener {
            timesClicked++
            btnClickMe.text = "Haha you clicked me!"
            locationHelper.getCurrentLocation(tvLatitude, tvLongitude)
            tvMyTextView.text = timesClicked.toString()
            Toast.makeText(this, "Hey Jakub!", Toast.LENGTH_LONG).show() // nice popup!
            mapHandler.setMarker(locationHelper.getLatitude(), locationHelper.getLongitude())
        }

        btnTraceControl.setOnClickListener {
            if(!sessionStarted) {
                sessionStarted = true
                btnTraceControl.text = "Stop"
                mapHandler.setStartTime(LocalTime.now())
                getLocationTask.run()
            }
            else
            {
                sessionStarted = false
                btnTraceControl.text = "Start"
                handler.removeCallbacksAndMessages(null)
            }
        }

    }

    private val getLocationTask = object : Runnable {
        override fun run() {
            if (sessionStarted) {
                locationHelper.getCurrentLocation(tvLatitude, tvLongitude)
                mapHandler.setMarker(locationHelper.getLatitude(), locationHelper.getLongitude())
                totalDistant.text = "Total distance: " + mapHandler.calculateTotalDistance().toString() + " m"
                val duration = Duration.between(mapHandler.getStartTime(), LocalTime.now())
                totalTime.text = "Total time: "+duration.toHours()+" h "+duration.toMinutes() % 60 + " m " + duration.seconds % 60 + " s"
                handler.postDelayed(this, delayMillis.toLong())
            }
        }
    }

}