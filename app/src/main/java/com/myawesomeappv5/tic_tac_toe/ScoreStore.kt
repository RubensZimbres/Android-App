package com.myawesomeappv5.tic_tac_toe

import android.content.Context
import androidx.core.content.edit

class ScoreStore(context: Context) {
    private val prefs = context.getSharedPreferences("scores", Context.MODE_PRIVATE)

    val wins: Int get() = prefs.getInt(KEY_WINS, 0)
    val losses: Int get() = prefs.getInt(KEY_LOSSES, 0)
    val ties: Int get() = prefs.getInt(KEY_TIES, 0)

    fun record(winner: Char?) {
        val key = when (winner) {
            TicTacToeGame.PLAYER -> KEY_WINS
            TicTacToeGame.COMPUTER -> KEY_LOSSES
            else -> KEY_TIES
        }
        prefs.edit { putInt(key, prefs.getInt(key, 0) + 1) }
    }

    private companion object {
        const val KEY_WINS = "wins"
        const val KEY_LOSSES = "losses"
        const val KEY_TIES = "ties"
    }
}
