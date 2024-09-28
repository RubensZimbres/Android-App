package com.myawesomeappv5.tic_tac_toe
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.widget.ImageView
import android.net.Uri
import android.content.ActivityNotFoundException

class WelcomeActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())
    private var currentVolume = 0.5f
    private val maxVolume = 1.0f
    private val fadeDuration = 510L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val rateStar: ImageView = findViewById(R.id.rate_star)
        rateStar.setOnClickListener {
            rateApp()
        }


        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val startGameButton = findViewById<Button>(R.id.startGameButton)

        // Initialize and start the MediaPlayer
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.game)
            if (mediaPlayer == null) {
                Log.e("WelcomeActivity", "MediaPlayer.create returned null")
                Toast.makeText(this, "Failed to create MediaPlayer", Toast.LENGTH_SHORT).show()
            } else {
                mediaPlayer.isLooping = true
                mediaPlayer.setVolume(0.5f, 0.5f) // Start with volume at 0
                mediaPlayer.start()
                startFadeIn()
                Log.d("WelcomeActivity", "MediaPlayer started successfully")
                Toast.makeText(this, "Sound should start fading in", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("WelcomeActivity", "Exception in MediaPlayer setup", e)
            Toast.makeText(this, "Error setting up MediaPlayer: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // Apply blinking animation to welcome text
        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        welcomeText.startAnimation(blinkAnimation)

        startGameButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun rateApp() {
        try {
            val marketUri = Uri.parse("market://details?id=$packageName")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            ////// FIX HERE PACKAGE NAME
            val playStoreUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            val webIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
            startActivity(webIntent)
        }
    }

    private fun startFadeIn() {
        currentVolume = 0f
        val volumeIncrement = maxVolume / (fadeDuration / 50) // Increase every 50ms

        val updateVolume = object : Runnable {
            override fun run() {
                if (currentVolume < maxVolume) {
                    currentVolume += volumeIncrement
                    if (currentVolume > maxVolume) currentVolume = maxVolume
                    mediaPlayer.setVolume(currentVolume, currentVolume)
                    handler.postDelayed(this, 50) // Run again after 50ms
                }
            }
        }

        handler.post(updateVolume)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Remove any pending posts of Runnable
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        handler.removeCallbacksAndMessages(null) // Stop the fade-in effect
    }

    override fun onResume() {
        super.onResume()
        if (::mediaPlayer.isInitialized && !mediaPlayer.isPlaying) {
            mediaPlayer.start()
            startFadeIn() // Restart the fade-in effect
        }
    }
}