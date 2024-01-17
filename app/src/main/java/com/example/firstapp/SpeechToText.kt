package com.example.firstapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class SpeechToText(private val context: Context, private val activity: Activity) {

    public lateinit var speechRecognizer: SpeechRecognizer
    public val handler = Handler()

    init {
        // Sprawdź i poproś o uprawnienie do nagrywania dźwięku
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            }
        }

        // Inicjalizacja SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    }

    public fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, activity.packageName)

        speechRecognizer.startListening(intent)
    }

    public fun stopListening()
    {
        handler.removeCallbacksAndMessages(null)
        speechRecognizer.stopListening()
        speechRecognizer.destroy()
    }


    fun onDestroy() {
        speechRecognizer.destroy()
        handler.removeCallbacksAndMessages(null)
    }
}

