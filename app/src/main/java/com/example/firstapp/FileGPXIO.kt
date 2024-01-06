package com.example.firstapp
import android.content.Context
import java.util.Date
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import androidx.core.content.FileProvider
import android.content.Intent
import android.app.DownloadManager
import android.os.Environment

class FileGPXIO {
    private var NumOfRecords: Int = 0
    private var FileName: String = generateFileName()

    private fun generateFileName(): String {
        val formattedDateTime = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "Runner_$formattedDateTime.gpx"
    }

    private fun writeDataToGpxFile(context: Context, runSession: RunSession): File {
        val file = File(context.getExternalFilesDir(null), FileName)
        val writer = FileWriter(file)

        writer.appendln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        writer.appendln("<gpx version=\"1.1\" creator=\"Runner\">")

        runSession.getMarkersList().forEachIndexed { index, marker ->
            val snippetData =
                marker.snippet?.split(", ")
            val time = snippetData?.get(0)?.substring(6)
            val laps = snippetData?.get(1)?.substring(6)
            val distance = snippetData?.get(2)?.substring(10)

            writer.appendln("<name>Point ${index + 1}</name>")
            writer.appendln("<wpt lat=\"${marker.position.latitude}\" lon=\"${marker.position.longitude}\">")
            writer.appendln("<time>$time</time>")
            writer.appendln("<lap>$laps</lap>")
            writer.appendln("<distance>$distance</distance>")
            writer.appendln("</wpt>")
        }

        writer.appendln("</gpx>")
        writer.close()

        return file
    }

    fun openGpxFile(context: Context): File {
        return File(context.getExternalFilesDir(null), FileName)
    }

    fun shareFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/xml"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            context.startActivity(Intent.createChooser(shareIntent, "Share GPX File"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("set_timerslack_ns")
    fun downloadGpxFile(context: Context, runSession: RunSession) {
        val file = writeDataToGpxFile(context, runSession)

        if (file != null) {
            try {
                val publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val newFile = File(publicDir, FileName)

                file.copyTo(newFile, overwrite = true)

                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

                val request = DownloadManager.Request(uri)
                request.setTitle("Download GPX File")
                request.setMimeType("text/xml")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FileName)

                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    }

