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

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        tvLatitude=findViewById(R.id.Latitude)
        tvLongitude=findViewById(R.id.Longitude)

        // TODO Kod odpowiedzialny za pobieranie lokalizacji wjebać do innego pliku
        // TODO Podpiąć getCurrentLocation() pod button a potem co 0.5sek aktualizować
        getCurrentLocation();


        val btnClickMe = findViewById<Button>(R.id.mybutton)
        val tvMyTextView = findViewById<TextView>(R.id.textView)
        var timesClicked = 0
        btnClickMe.text = "Haha" // ustawienie atrybutu tekstu
        btnClickMe.setOnClickListener {
            timesClicked++
            btnClickMe.text = "Haha you clicked me!"

            tvMyTextView.text = timesClicked.toString()
            Toast.makeText(this, "Hey Jakub!", Toast.LENGTH_LONG).show() // nice popup!
        }
    }

    private fun getCurrentLocation()
    {
        if(checkPermissions())
        {
            if(isLocationEnabled())
            {
                //final latitude and longitude code here
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){
                    task->
                    val location: Location?=task.result
                    if(location==null)
                    {
                        Toast.makeText(this, "Null Received", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        Toast.makeText(this, "Get Success", Toast.LENGTH_LONG).show()
                        tvLatitude.text=""+location.latitude
                        tvLongitude.text=""+location.longitude
                    }
                }
            }
            else
            {
                //setting open here
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent= Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)

            }
        }
        else
        {
            //request permission here
            requestPermission()
        }
    }

    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission()
    {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION=100
    }

    private fun checkPermissions(): Boolean
    {
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode== PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(applicationContext, "Granted", Toast.LENGTH_LONG).show()
                getCurrentLocation()
            }
            else{
                Toast.makeText(applicationContext, "Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

}