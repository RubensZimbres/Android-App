package com.myawesomeappv5.tic_tac_toe

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class WinActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win)

        val resultTextView = findViewById<TextView>(R.id.winnerText)
        val firstMark = findViewById<CellView>(R.id.resultMark1)
        val secondMark = findViewById<CellView>(R.id.resultMark2)

        when (intent.getStringExtra(EXTRA_WINNER)) {
            TicTacToeGame.PLAYER.toString() -> {
                resultTextView.setText(R.string.result_you_win)
                resultTextView.setTextColor(ContextCompat.getColor(this, R.color.mark_x))
                firstMark.setMark(TicTacToeGame.PLAYER, animate = true)
                secondMark.visibility = View.GONE
            }
            TicTacToeGame.COMPUTER.toString() -> {
                resultTextView.setText(R.string.result_computer_wins)
                resultTextView.setTextColor(ContextCompat.getColor(this, R.color.mark_o))
                firstMark.setMark(TicTacToeGame.COMPUTER, animate = true)
                secondMark.visibility = View.GONE
            }
            else -> {
                resultTextView.setText(R.string.result_tie)
                resultTextView.setTextColor(ContextCompat.getColor(this, R.color.fire_amber))
                firstMark.setMark(TicTacToeGame.PLAYER, animate = true)
                secondMark.setMark(TicTacToeGame.COMPUTER, animate = true)
            }
        }

        val scores = ScoreStore(this)
        findViewById<TextView>(R.id.scoreSummary).text =
            getString(R.string.score_summary, scores.wins, scores.ties, scores.losses)

        findViewById<View>(R.id.resultCard).apply {
            scaleX = 0.8f
            scaleY = 0.8f
            alpha = 0f
            animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(450)
                .setInterpolator(OvershootInterpolator())
                .start()
        }

        findViewById<Button>(R.id.playAgainButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

        findViewById<Button>(R.id.exitButton).setOnClickListener {
            finishAffinity()
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.end_game)?.apply { start() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        const val EXTRA_WINNER = "WINNER"
        const val TIE = "TIE"
    }
}
