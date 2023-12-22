package com.example.firstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.google.android.gms.maps.SupportMapFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.Duration
import java.time.LocalTime

class MainActivity : AppCompatActivity() {

    /*
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

    private var sessionStarted : Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private val delayMillis = 350
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainFragment,
            )
        )
        //setSupportActionBar(navController);
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        setupActionBarWithNavController(navController, appBarConfiguration)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        /*

        tvCurrentPos=findViewById(R.id.currentPos)
        tvLastPos=findViewById(R.id.lastPoint)
        totalDistant=findViewById(R.id.totalDistance)
        totalTime=findViewById(R.id.totalTime)
        laps=findViewById(R.id.okrazenia)
        pointsNum=findViewById(R.id.pointsNum)
        voiceSwitch=findViewById(R.id.voiceSwitch)
        locationHelper = LocationHelper(this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapHandler = MapHandler(mapFragment)

        //val btnClickMe = findViewById<Button>(R.id.mybutton)
        val btnTraceControl = findViewById<Button>(R.id.traceControlButton)
        //var timesClicked = 0
        //btnClickMe.text = "Haha" // ustawienie atrybutu tekstu

        // TODO Kod odpowiedzialny za pobieranie lokalizacji wjebać do innego pliku - Done
        // TODO Podpiąć getCurrentLocation() pod button a potem co 0.5sek aktualizować - Done
        // TODO Dodac zliczanie okrazen do MapHandlera
        /* TODO UtworzycKlase RunSession która będzie reprezentowała dany bieg usera
            - markery z MapHandlera
            - odczyt odlegosci
            - odczyt czasu
            - odczyt pokonanych okrazen
         */
        // TODO przepisac kod na MVVM


        locationHelper.getCurrentLocation(tvCurrentPos)
        /*
        btnClickMe.setOnClickListener {
            timesClicked++
            btnClickMe.text = "Haha you clicked me!"
            locationHelper.getCurrentLocation(tvLatitude, tvLongitude)
            tvMyTextView.text = timesClicked.toString()
            Toast.makeText(this, "Hey Jakub!", Toast.LENGTH_LONG).show() // nice popup!
            mapHandler.setMarker(locationHelper.getLatitude(), locationHelper.getLongitude())
        }
        */
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
                tvLastPos.text = "" + locationHelper.getLatitude() + " " + locationHelper.getLongitude()
                locationHelper.getCurrentLocation(tvCurrentPos)
                mapHandler.setMarker(locationHelper.getLatitude(), locationHelper.getLongitude())
                totalDistant.text = "" + mapHandler.calculateTotalDistance().toString() + " m"
                val duration = Duration.between(mapHandler.getStartTime(), LocalTime.now())
                totalTime.text = "" + duration.toHours()+" h "+duration.toMinutes() % 60 + " m " + duration.seconds % 60 + " s"
                pointsNum.text = "" + mapHandler.getMarkersCount()
                handler.postDelayed(this, delayMillis.toLong())
            }
        }*/
    }

}