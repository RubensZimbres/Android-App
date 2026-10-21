package com.myawesomeappv5.tic_tac_toe

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class WelcomeActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var currentVolume = 0f
    private val maxVolume = 1.0f
    private val fadeDuration = 2000L
    private var playPulse: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        applyFireGradient(findViewById(R.id.welcomeTitle))

        findViewById<View>(R.id.rateButton).setOnClickListener { rateApp() }

        val startGameButton = findViewById<View>(R.id.startGameButton)
        startGameButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        playPulse = ObjectAnimator.ofPropertyValuesHolder(
            startGameButton,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.06f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.06f)
        ).apply {
            duration = 900
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.game)?.apply {
            isLooping = true
            setVolume(0f, 0f)
            start()
        }
        if (mediaPlayer == null) {
            Log.e("WelcomeActivity", "MediaPlayer.create returned null")
        } else {
            startFadeIn()
        }
    }

    private fun applyFireGradient(title: TextView) {
        title.paint.shader = LinearGradient(
            0f, 0f, 0f, title.textSize,
            intArrayOf(
                ContextCompat.getColor(this, R.color.fire_amber),
                ContextCompat.getColor(this, R.color.fire_orange),
                ContextCompat.getColor(this, R.color.fire_red)
            ),
            null,
            Shader.TileMode.CLAMP
        )
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
            val playStoreUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            val webIntent = Intent(Intent.ACTION_VIEW, playStoreUri)
            startActivity(webIntent)
        }
    }

    private fun startFadeIn() {
        val player = mediaPlayer ?: return
        currentVolume = 0f
        val volumeIncrement = maxVolume / (fadeDuration / 50)

        val updateVolume = object : Runnable {
            override fun run() {
                if (currentVolume < maxVolume) {
                    currentVolume += volumeIncrement
                    if (currentVolume > maxVolume) currentVolume = maxVolume
                    player.setVolume(currentVolume, currentVolume)
                    handler.postDelayed(this, 50)
                }
            }
        }

        handler.post(updateVolume)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        playPulse?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.takeIf { it.isPlaying }?.pause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.takeIf { !it.isPlaying }?.let {
            it.start()
            startFadeIn()
        }
    }
}
