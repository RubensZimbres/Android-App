package com.myawesomeappv5.tic_tac_toe

import com.myawesomeappv5.tic_tac_toe.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var buttons: Array<Array<Button>>
    private var playerTurn = true
    private var roundCount = 0
    private var gameEnded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttons = Array(3) { row ->
            Array(3) { col ->
                findViewById<Button>(
                    resources.getIdentifier(
                        "button_$row$col",
                        "id",
                        packageName
                    )
                )
            }
        }

        for (row in buttons) {
            for (button in row) {
                button.setOnClickListener { onButtonClick(button) }
            }
        }
    }

    private fun onButtonClick(button: Button) {
        if (button.text.toString() != "" || !playerTurn || gameEnded) return

        makeMove(button, "X", Color.BLUE)

        if (!gameEnded && roundCount < 9) {
            playerTurn = false
            Handler(Looper.getMainLooper()).postDelayed({
                computerMove()
                playerTurn = true
            }, 500)
        }
    }

    private fun makeMove(button: Button, symbol: String, color: Int) {
        button.text = symbol
        button.setTextColor(color)
        roundCount++

        val winningLine = checkForWin(symbol)
        if (winningLine != null) {
            gameEnded = true
            animateWinningLine(winningLine)
            if (symbol == "X") {
                playerWins()
            } else {
                computerWins()
            }
        } else if (roundCount == 9) {
            gameEnded = true
            tie()
        }
    }

    private fun computerMove() {
        if (gameEnded) return

        // Try to win
        if (findWinningMove("O")) return

        // Block player's win
        if (findWinningMove("X")) return

        // Take center if available
        if (buttons[1][1].text == "") {
            makeMove(buttons[1][1], "O", Color.RED)
            return
        }

        // Take a corner
        val corners = listOf(Pair(0,0), Pair(0,2), Pair(2,0), Pair(2,2))
        for ((i, j) in corners.shuffled()) {
            if (buttons[i][j].text == "") {
                makeMove(buttons[i][j], "O", Color.RED)
                return
            }
        }

        // Take any available space
        for (i in 0..2) {
            for (j in 0..2) {
                if (buttons[i][j].text == "") {
                    makeMove(buttons[i][j], "O", Color.RED)
                    return
                }
            }
        }
    }

    private fun findWinningMove(symbol: String): Boolean {
        for (i in 0..2) {
            for (j in 0..2) {
                if (buttons[i][j].text == "") {
                    buttons[i][j].text = symbol
                    if (checkForWin(symbol) != null) {
                        buttons[i][j].text = ""
                        makeMove(buttons[i][j], "O", Color.RED)
                        return true
                    }
                    buttons[i][j].text = ""
                }
            }
        }
        return false
    }

    private fun checkForWin(symbol: String): List<Button>? {
        // Check rows
        for (i in 0..2) {
            if (buttons[i][0].text == symbol && buttons[i][1].text == symbol && buttons[i][2].text == symbol) {
                return listOf(buttons[i][0], buttons[i][1], buttons[i][2])
            }
        }

        // Check columns
        for (i in 0..2) {
            if (buttons[0][i].text == symbol && buttons[1][i].text == symbol && buttons[2][i].text == symbol) {
                return listOf(buttons[0][i], buttons[1][i], buttons[2][i])
            }
        }

        // Check diagonals
        if (buttons[0][0].text == symbol && buttons[1][1].text == symbol && buttons[2][2].text == symbol) {
            return listOf(buttons[0][0], buttons[1][1], buttons[2][2])
        }

        if (buttons[0][2].text == symbol && buttons[1][1].text == symbol && buttons[2][0].text == symbol) {
            return listOf(buttons[0][2], buttons[1][1], buttons[2][0])
        }

        return null
    }

    private fun animateWinningLine(winningButtons: List<Button>) {
        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        winningButtons.forEach { it.startAnimation(blinkAnimation) }
    }

    private fun playerWins() {
        delayedNavigateToWinScreen("X")
    }

    private fun computerWins() {
        delayedNavigateToWinScreen("O")
    }

    private fun tie() {
        navigateToWinScreen("TIE")
    }

    private fun delayedNavigateToWinScreen(winner: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToWinScreen(winner)
        }, 1500) // Delay for 1.5 seconds to show the blinking animation
    }

    private fun navigateToWinScreen(winner: String) {
        val intent = Intent(this, WinActivity::class.java)
        intent.putExtra("WINNER", winner)
        startActivity(intent)
        finish()
    }
}