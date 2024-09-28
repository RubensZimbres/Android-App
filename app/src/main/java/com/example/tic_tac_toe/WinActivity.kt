package com.myawesomeappv5.tic_tac_toe

import android.media.MediaPlayer
import android.view.animation.AnimationUtils
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WinActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win)

        val resultTextView = findViewById<TextView>(R.id.winnerText)

        val winner = intent.getStringExtra("WINNER")
        val resultText = when (winner) {
            "X" -> "Player X Wins!"
            "O" -> "Player O Wins!"
            else -> "It's a Tie!"
        }
        resultTextView.text = resultText

        // Apply blinking animation to result text
        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        resultTextView.startAnimation(blinkAnimation)

        findViewById<Button>(R.id.playAgainButton).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.exitButton).setOnClickListener {
            finishAffinity()
        }

        // Initialize and start the MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.end_game)
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the MediaPlayer resources
        mediaPlayer.release()
    }
}