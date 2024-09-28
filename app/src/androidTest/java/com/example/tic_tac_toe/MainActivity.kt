import com.myawesomeappv5.tic_tac_toe.R
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var buttons: Array<Array<Button>>
    private var player1Turn = true
    private var roundCount = 0

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
        if (button.text.toString() != "") return

        if (player1Turn) {
            button.text = "X"
        } else {
            button.text = "O"
        }

        roundCount++

        if (checkForWin()) {
            if (player1Turn) {
                player1Wins()
            } else {
                player2Wins()
            }
        } else if (roundCount == 9) {
            draw()
        } else {
            player1Turn = !player1Turn
        }
    }

    private fun checkForWin(): Boolean {
        val field = Array(3) { row ->
            Array(3) { col ->
                buttons[row][col].text.toString()
            }
        }

        for (i in 0..2) {
            if (field[i][0] == field[i][1] && field[i][0] == field[i][2] && field[i][0] != "") {
                return true
            }
            if (field[0][i] == field[1][i] && field[0][i] == field[2][i] && field[0][i] != "") {
                return true
            }
        }

        if (field[0][0] == field[1][1] && field[0][0] == field[2][2] && field[0][0] != "") {
            return true
        }

        if (field[0][2] == field[1][1] && field[0][2] == field[2][0] && field[0][2] != "") {
            return true
        }

        return false
    }

    private fun player1Wins() {
        Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT).show()
        resetBoard()
    }

    private fun player2Wins() {
        Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show()
        resetBoard()
    }

    private fun draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show()
        resetBoard()
    }

    private fun resetBoard() {
        for (row in buttons) {
            for (button in row) {
                button.text = ""
            }
        }
        roundCount = 0
        player1Turn = true
    }
}