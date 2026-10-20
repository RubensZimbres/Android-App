package com.myawesomeappv5.tic_tac_toe

import kotlin.random.Random

/**
 * Board state and computer strategy, kept free of Android classes so it can be unit-tested.
 * Cells are indexed 0..8, row by row.
 */
class TicTacToeGame(state: String = EMPTY.toString().repeat(9)) {
    private val board = state.toCharArray()

    init {
        require(board.size == 9 && board.all { it == EMPTY || it == PLAYER || it == COMPUTER }) {
            "Invalid board state: '$state'"
        }
    }

    val isFull: Boolean get() = board.none { it == EMPTY }
    val isOver: Boolean get() = winningLine() != null || isFull

    /** The player always starts, so it's the computer's turn whenever X has one more mark than O. */
    val isComputerTurn: Boolean
        get() = !isOver && board.count { it == PLAYER } > board.count { it == COMPUTER }

    operator fun get(index: Int): Char = board[index]

    fun isEmpty(index: Int): Boolean = board[index] == EMPTY

    /** Places [mark] at [index]. Returns false if the move isn't allowed. */
    fun play(index: Int, mark: Char): Boolean {
        if (isOver || !isEmpty(index)) return false
        board[index] = mark
        return true
    }

    /** The three cells of the first completed line, or null if nobody has won. */
    fun winningLine(): IntArray? = LINES.firstOrNull { line ->
        board[line[0]] != EMPTY && line.all { board[it] == board[line[0]] }
    }

    /** [PLAYER], [COMPUTER], or null for a game still running or tied. */
    fun winner(): Char? = winningLine()?.let { board[it[0]] }

    /**
     * Win if possible, otherwise block the player, otherwise take the center,
     * otherwise a random corner, otherwise the first free cell.
     */
    fun chooseComputerMove(random: Random = Random.Default): Int {
        check(!isOver) { "Game is already over" }

        findCompletingMove(COMPUTER)?.let { return it }
        findCompletingMove(PLAYER)?.let { return it }
        if (isEmpty(CENTER)) return CENTER
        CORNERS.shuffled(random).firstOrNull { isEmpty(it) }?.let { return it }
        return board.indices.first { isEmpty(it) }
    }

    /** A free cell that would complete a line for [mark], if any. */
    private fun findCompletingMove(mark: Char): Int? = board.indices.firstOrNull { index ->
        isEmpty(index) && LINES.any { line ->
            index in line && line.all { it == index || board[it] == mark }
        }
    }

    override fun toString(): String = String(board)

    companion object {
        const val EMPTY = ' '
        const val PLAYER = 'X'
        const val COMPUTER = 'O'

        private const val CENTER = 4
        private val CORNERS = listOf(0, 2, 6, 8)
        private val LINES = listOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8), // rows
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8), // columns
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)                       // diagonals
        )
    }
}
