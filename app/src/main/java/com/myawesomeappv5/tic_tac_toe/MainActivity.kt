package com.myawesomeappv5.tic_tac_toe

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var cells: List<CellView>
    private lateinit var turnText: TextView
    private lateinit var scoreStore: ScoreStore
    private var game = TicTacToeGame()

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var soundPool: SoundPool
    private var playerSoundId = 0
    private var computerSoundId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        turnText = findViewById(R.id.turnText)
        scoreStore = ScoreStore(this)
        cells = CELL_IDS.map { findViewById(it) }
        cells.forEachIndexed { index, cell ->
            cell.setOnClickListener { onCellClick(index) }
        }

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
        playerSoundId = soundPool.load(this, R.raw.move_x, 1)
        computerSoundId = soundPool.load(this, R.raw.move_o, 1)

        // Restore a game in progress, e.g. after the screen was rotated.
        savedInstanceState?.getString(KEY_BOARD)?.let { game = TicTacToeGame(it) }
        cells.forEachIndexed { index, cell -> cell.setMark(game[index], animate = false) }
        updateScoreboard()

        when {
            game.isOver -> showResult()
            game.isComputerTurn -> scheduleComputerMove()
            else -> showTurn(playerTurn = true)
        }
    }

    private fun onCellClick(index: Int) {
        if (game.isComputerTurn || !game.play(index, TicTacToeGame.PLAYER)) return

        cells[index].setMark(TicTacToeGame.PLAYER, animate = true)
        soundPool.play(playerSoundId, 1f, 1f, 1, 0, 1f)

        if (game.isOver) onGameOver() else scheduleComputerMove()
    }

    private fun scheduleComputerMove() {
        showTurn(playerTurn = false)
        handler.postDelayed({
            val index = game.chooseComputerMove()
            game.play(index, TicTacToeGame.COMPUTER)
            cells[index].setMark(TicTacToeGame.COMPUTER, animate = true)
            soundPool.play(computerSoundId, 1f, 1f, 1, 0, 1f)

            if (game.isOver) onGameOver() else showTurn(playerTurn = true)
        }, COMPUTER_DELAY_MS)
    }

    private fun onGameOver() {
        scoreStore.record(game.winner())
        updateScoreboard()

        val winningLine = game.winningLine()
        if (winningLine != null) {
            winningLine.forEach { cells[it].setHighlighted(true) }
            turnText.text = getString(
                if (game.winner() == TicTacToeGame.PLAYER) R.string.result_you_win else R.string.result_computer_wins
            )
        } else {
            turnText.text = getString(R.string.result_tie)
        }
        // Give the player a moment to see the final board before switching screens.
        handler.postDelayed({ showResult() }, if (winningLine != null) WIN_DELAY_MS else TIE_DELAY_MS)
    }

    private fun showTurn(playerTurn: Boolean) {
        turnText.text = getString(if (playerTurn) R.string.turn_player else R.string.turn_computer)
        turnText.setTextColor(
            ContextCompat.getColor(this, if (playerTurn) R.color.mark_x else R.color.mark_o)
        )
    }

    private fun updateScoreboard() {
        findViewById<TextView>(R.id.scoreWins).text = scoreStore.wins.toString()
        findViewById<TextView>(R.id.scoreTies).text = scoreStore.ties.toString()
        findViewById<TextView>(R.id.scoreLosses).text = scoreStore.losses.toString()
    }

    private fun showResult() {
        val intent = Intent(this, WinActivity::class.java)
        intent.putExtra(WinActivity.EXTRA_WINNER, game.winner()?.toString() ?: WinActivity.TIE)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_BOARD, game.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        // Drop any pending computer move or screen change so they can't run after we're gone.
        handler.removeCallbacksAndMessages(null)
        soundPool.release()
    }

    private companion object {
        const val KEY_BOARD = "board"
        const val COMPUTER_DELAY_MS = 500L
        const val WIN_DELAY_MS = 1500L
        const val TIE_DELAY_MS = 900L

        val CELL_IDS = listOf(
            R.id.cell_00, R.id.cell_01, R.id.cell_02,
            R.id.cell_10, R.id.cell_11, R.id.cell_12,
            R.id.cell_20, R.id.cell_21, R.id.cell_22
        )
    }
}
