package com.example.firstapp.ui
import SharedViewModel
import android.widget.Button
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.firstapp.R
import com.example.firstapp.FileGPXIO
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import android.graphics.Color
import com.github.mikephil.charting.components.XAxis

class StatsFragment : Fragment() {
    private lateinit var barChart: BarChart
    private val fileGpxIO = FileGPXIO()
    private lateinit var sharedViewModel: SharedViewModel
    companion object {
        private const val MY_PERMISSIONS_REQUEST_STORAGE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barChart = view.findViewById(R.id.barchart)

        val downloadButton = view.findViewById<Button>(R.id.download_button)
        val shareButton = view.findViewById<ImageButton>(R.id.share_button)
        downloadButton.setOnClickListener {
            if (checkPermissions()) {
                sharedViewModel.runSession?.let { session ->
                    fileGpxIO.downloadGpxFile(requireContext(), session)
                }
            } else {
                requestPermissions()
            }
        }

        shareButton.setOnClickListener {
            if (checkPermissions()) {
                sharedViewModel.runSession?.let { session ->
                    fileGpxIO.shareFile(requireContext(), session)
                }
            } else {
                requestPermissions()
            }
        }

        sharedViewModel.sessionStoppedLiveData.observe(viewLifecycleOwner) { sessionStopped ->
            if (sessionStopped) {
                println("DATA TO HISTOGRAM!!!!!!!!!!!!!!!!!!:")
                val histogramData = prepareChartData()
                generateChart(histogramData)

                sharedViewModel.setSessionStopped(false)
            }
        }
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

        ActivityCompat.requestPermissions(requireActivity(), permissions,
            MY_PERMISSIONS_REQUEST_STORAGE
        )
    }

//    private fun generateHistogram() {
//        val values = mutableListOf<BarEntry>()
//        for (i in 1..10) {
//            val randomValue = (190f + (Math.random() * 20)).toFloat()
//            values.add(BarEntry(randomValue, i.toFloat()))
//        }
//
//        val barDataSet = BarDataSet(values, "Temp hist for test with random data")
//        barDataSet.color = Color.BLUE
//
//        val dataSets = mutableListOf<IBarDataSet>()
//        dataSets.add(barDataSet)
//
//        val data = BarData(dataSets)
//        barChart.data = data
//        barChart.setFitBars(true)
//
//        barChart.description.isEnabled = false
//        barChart.setDrawValueAboveBar(true)
//        barChart.xAxis.valueFormatter = null
//        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//        barChart.xAxis.setDrawGridLines(false)
//        barChart.axisLeft.setDrawGridLines(false)
//        barChart.axisRight.isEnabled = false
//        barChart.animateY(1000)
//    }

    private fun generateChart(values: List<BarEntry>) {
        val barDataSet = BarDataSet(values, "Number of laps of a given length")
        barDataSet.color = Color.BLUE

        val dataSets = mutableListOf<IBarDataSet>()
        dataSets.add(barDataSet)

        val data = BarData(dataSets)
        barChart.data = data
        barChart.setFitBars(true)

        barChart.description.isEnabled = false
        barChart.setDrawValueAboveBar(true)
        barChart.xAxis.valueFormatter = null
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        barChart.animateY(1000)
    }

    private fun prepareChartData(): List<BarEntry> {
        val distanceArray = mutableListOf<Float>()

        sharedViewModel.runSession?.let { session ->
            var latestLap: String? = null
            var lastDistance: Int? = null

            val lastDistanceList = mutableListOf<Int>()

            session.getMarkersList().forEachIndexed { _, marker ->
                val snippetData = marker.snippet?.split(", ")
                val lap = snippetData?.get(1)?.substring(6)
                val distanceText = snippetData?.get(2)?.substringBefore(" meters")
                val distanceValue = distanceText?.toIntOrNull() ?: 0

                if (lap != latestLap) {
                    lastDistance?.let { lastDistanceList.add(it) }

                    latestLap = lap
                    lastDistance = distanceValue
                } else {
                    lastDistance = distanceValue
                }
            }

            lastDistance?.let { lastDistanceList.add(it) }

            // Przykładowe DANE!! - trzeba wywalic jak bedzie dzialac przypisanie
            for (i in 1..15) {
                val randomValue = (180f + (Math.random() * 40)).toFloat()
                distanceArray.add(randomValue)
            }

            lastDistanceList.forEach { distanceArray.add(it.toFloat()) }
        }


        val amountOfOccurrences = countNearData(distanceArray)

        println(distanceArray)
        println(amountOfOccurrences)

        return amountOfOccurrences.map { BarEntry(it.key, it.value.toFloat()) }
    }

    fun countNearData(lista: MutableList<Float>): Map<Float, Int> {
        val diffInMeters = 1.0f
        val amountOfApperances = mutableMapOf<Float, Int>()

        for (dane in lista) {
            val roundedData = dane.roundTo(diffInMeters)
            amountOfApperances[roundedData] = amountOfApperances.getOrDefault(roundedData, 0) + 1
        }

        return amountOfApperances.toMap()
    }

    fun Float.roundTo(diffInMeters: Float): Float {
        return (this / diffInMeters).toInt().toFloat() * diffInMeters
    }
}
