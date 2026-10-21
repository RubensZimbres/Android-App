package com.myawesomeappv5.tic_tac_toe

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class TicTacToeGameTest {

    @Test
    fun detectsRowColumnAndDiagonalWins() {
        assertArrayEquals(intArrayOf(3, 4, 5), TicTacToeGame("OO XXX   ").winningLine())
        assertArrayEquals(intArrayOf(1, 4, 7), TicTacToeGame("OX  XO X ").winningLine())
        assertArrayEquals(intArrayOf(2, 4, 6), TicTacToeGame("XXO O OX ").winningLine())
        assertEquals(TicTacToeGame.COMPUTER, TicTacToeGame("XXO O OX ").winner())
    }

    @Test
    fun fullBoardWithoutLineIsATie() {
        val game = TicTacToeGame("XOXXOOOXX")
        assertTrue(game.isOver)
        assertNull(game.winner())
    }

    @Test
    fun rejectsMovesOnTakenCellsAndFinishedGames() {
        val game = TicTacToeGame()
        assertTrue(game.play(0, TicTacToeGame.PLAYER))
        assertFalse(game.play(0, TicTacToeGame.COMPUTER))

        val finished = TicTacToeGame("XXXOO    ")
        assertFalse(finished.play(8, TicTacToeGame.COMPUTER))
    }

    @Test
    fun computerTurnFollowsMarkCount() {
        assertFalse(TicTacToeGame().isComputerTurn)
        assertTrue(TicTacToeGame("X        ").isComputerTurn)
        assertFalse(TicTacToeGame("XO       ").isComputerTurn)
    }

    @Test
    fun computerPrefersWinningOverBlocking() {
        assertEquals(5, TicTacToeGame("XX OO X  ").chooseComputerMove())
    }

    @Test
    fun computerBlocksPlayer() {
        assertEquals(2, TicTacToeGame("XX  O    ").chooseComputerMove())
    }

    @Test
    fun computerTakesCenterThenCorner() {
        assertEquals(4, TicTacToeGame("X        ").chooseComputerMove())
        val corner = TicTacToeGame("    X    ").chooseComputerMove(Random(1))
        assertTrue(corner in listOf(0, 2, 6, 8))
    }

    @Test
    fun boardStateSurvivesSerialization() {
        val game = TicTacToeGame()
        game.play(4, TicTacToeGame.PLAYER)
        game.play(0, TicTacToeGame.COMPUTER)
        assertEquals("O   X    ", TicTacToeGame(game.toString()).toString())
    }
}
