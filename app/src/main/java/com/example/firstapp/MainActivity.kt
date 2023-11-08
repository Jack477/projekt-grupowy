package com.example.firstapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.firstapp.LocationHelper

class MainActivity : AppCompatActivity() {

    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLatitude=findViewById(R.id.Latitude)
        tvLongitude=findViewById(R.id.Longitude)
        locationHelper = LocationHelper(this)

        // TODO Kod odpowiedzialny za pobieranie lokalizacji wjebać do innego pliku
        // TODO Podpiąć getCurrentLocation() pod button a potem co 0.5sek aktualizować

        val btnClickMe = findViewById<Button>(R.id.mybutton)
        val tvMyTextView = findViewById<TextView>(R.id.textView)
        var timesClicked = 0
        btnClickMe.text = "Haha" // ustawienie atrybutu tekstu
        btnClickMe.setOnClickListener {
            timesClicked++
            btnClickMe.text = "Haha you clicked me!"
            locationHelper.getCurrentLocation(tvLatitude, tvLongitude)
            tvMyTextView.text = timesClicked.toString()
            Toast.makeText(this, "Hey Jakub!", Toast.LENGTH_LONG).show() // nice popup!
        }
    }

}