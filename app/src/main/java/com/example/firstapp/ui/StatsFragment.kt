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

class StatsFragment : Fragment() {
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
}
