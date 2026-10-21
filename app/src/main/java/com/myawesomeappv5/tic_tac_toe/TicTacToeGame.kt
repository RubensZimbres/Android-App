package com.myawesomeappv5.tic_tac_toe

import kotlin.random.Random

class TicTacToeGame(state: String = EMPTY.toString().repeat(9)) {
    private val board = state.toCharArray()

    init {
        require(board.size == 9 && board.all { it == EMPTY || it == PLAYER || it == COMPUTER }) {
            "Invalid board state: '$state'"
        }
    }

    val isFull: Boolean get() = board.none { it == EMPTY }
    val isOver: Boolean get() = winningLine() != null || isFull

    val isComputerTurn: Boolean
        get() = !isOver && board.count { it == PLAYER } > board.count { it == COMPUTER }

    operator fun get(index: Int): Char = board[index]

    fun isEmpty(index: Int): Boolean = board[index] == EMPTY

    fun play(index: Int, mark: Char): Boolean {
        if (isOver || !isEmpty(index)) return false
        board[index] = mark
        return true
    }

    fun winningLine(): IntArray? = LINES.firstOrNull { line ->
        board[line[0]] != EMPTY && line.all { board[it] == board[line[0]] }
    }

    fun winner(): Char? = winningLine()?.let { board[it[0]] }

    fun chooseComputerMove(random: Random = Random.Default): Int {
        check(!isOver) { "Game is already over" }

        findCompletingMove(COMPUTER)?.let { return it }
        findCompletingMove(PLAYER)?.let { return it }
        if (isEmpty(CENTER)) return CENTER
        CORNERS.shuffled(random).firstOrNull { isEmpty(it) }?.let { return it }
        return board.indices.first { isEmpty(it) }
    }

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
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)
        )
    }
}
