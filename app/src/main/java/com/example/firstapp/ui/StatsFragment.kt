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
import android.widget.TextView
import com.example.firstapp.RunSession
import com.github.mikephil.charting.components.XAxis

class StatsFragment : Fragment() {
    private lateinit var barChart: BarChart
    private var selectedSessionIndex = -1
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
        if (checkPermissions()) {
            //.downloadGpxFile(requireContext(), sessions[sessionId - 1])
        } else {
            requestPermissions()
        }
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
        val plusButton = view.findViewById<Button>(R.id.plus_button)
        val minusButton = view.findViewById<Button>(R.id.minus_button)
        val availableText = view.findViewById<TextView>(R.id.available)
        val selectedText = view.findViewById<TextView>(R.id.selected)
        availableText.text = sharedViewModel.runSession.size.toString()

        updateSelectedText(selectedText)

        plusButton.setOnClickListener {
            if (selectedSessionIndex < sharedViewModel.runSession.size - 1) {
                selectedSessionIndex++
                updateSelectedText(selectedText)
                updateChart()
            }
        }

        minusButton.setOnClickListener {
            if (selectedSessionIndex > -1) {
                selectedSessionIndex--
                updateSelectedText(selectedText)
                updateChart()
            }
        }

        downloadButton.setOnClickListener {
            if (checkPermissions()) {
                if (sharedViewModel.runSession.isNotEmpty() && selectedSessionIndex >= 0) {
                    val session = sharedViewModel.runSession[selectedSessionIndex]
                    fileGpxIO.downloadGpxFile(requireContext(), session)
                }
            } else {
                requestPermissions()
            }
        }

        shareButton.setOnClickListener {
            if (checkPermissions()) {
                if (sharedViewModel.runSession.isNotEmpty() && selectedSessionIndex >= 0) {
                    val session = sharedViewModel.runSession[selectedSessionIndex]
                    fileGpxIO.shareFile(requireContext(), session)
                }
            } else {
                requestPermissions()
            }
        }

        sharedViewModel.sessionStoppedLiveData.observe(viewLifecycleOwner) { sessionStopped ->
            if (sessionStopped) {
                sharedViewModel.runSession.lastOrNull()?.let { latestSession ->
                    val histogramData = prepareChartData(latestSession)
                    generateChart(histogramData)
                }

                sharedViewModel.setSessionStopped(false)
            }
        }
    }

    private fun updateSelectedText(selectedText: TextView) {
        val sessionNumber = selectedSessionIndex + 1
        selectedText.text = if (selectedSessionIndex >= 0) sessionNumber.toString() else "0"
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

    private fun prepareChartData(runSession: RunSession): List<BarEntry> {
        val maxDistancePerLap = mutableMapOf<String, Float>()

        runSession.getMarkersList().forEachIndexed { _, marker ->
            val snippetData = marker.snippet?.split(", ")
            val lap = snippetData?.get(1)?.substring(6)
            val distanceText = snippetData?.get(2)?.substringBefore(" meters")
            val distanceValue = distanceText?.substringAfter("Distance:")?.trim()?.toFloatOrNull()
            if (distanceValue != null && lap != null) {
                // Max value for lap
                val currentMax = maxDistancePerLap[lap] ?: 0f
                if (distanceValue > currentMax) {
                    maxDistancePerLap[lap] = distanceValue
                }
            }
        }

        return maxDistancePerLap.map { BarEntry(it.key.toFloat(), it.value) }
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

    private fun updateChart() {
        val sessionData = if (selectedSessionIndex >= 0 && sharedViewModel.runSession.size > selectedSessionIndex) {
            sharedViewModel.runSession[selectedSessionIndex]
        } else {
            sharedViewModel.runSession.lastOrNull()
        }

        sessionData?.let {
            val chartData = prepareChartData(it)
            generateChart(chartData)
        }
    }

    fun Float.roundTo(diffInMeters: Float): Float {
        return (this / diffInMeters).toInt().toFloat() * diffInMeters
    }
}
