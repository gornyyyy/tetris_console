package org.example

import org.example.model.Board
import org.example.ui.ConsoleRenderer
import kotlin.system.exitProcess

class Game {
    private val board = Board()
    private val renderer = ConsoleRenderer()

    private var currentShape = board.spawnShape()
    private var nextShape = board.spawnShape()

    private var score = 0
    private var level = 1

    @Volatile var isRunning = true
        private set

    var fallDelay = 500L
        private set

    // --- Состояние задержки фиксации (Lock Delay) ---
    private var isLocking = false
    private var lockTimer = 0L
    private val lockDelayMs = 500L

    fun start() {
        renderer.renderStartScreen()
    }

    fun tick() {
        if (!isRunning) return

        if (isLocking) {
            if (System.currentTimeMillis() - lockTimer >= lockDelayMs) {

                lockAndSpawn()
            }
        } else {
            if (!board.tryMove(currentShape, 0, 1)) {
                isLocking = true
                lockTimer = System.currentTimeMillis()
            }
        }

        draw()
    }

    fun handleInput(action: PlayerAction) {
        if (!isRunning) return

        var moved = false

        when (action) {
            PlayerAction.MOVE_LEFT  -> moved = board.tryMove(currentShape, -1, 0)
            PlayerAction.MOVE_RIGHT -> moved = board.tryMove(currentShape, 1, 0)
            PlayerAction.MOVE_DOWN  -> {
                if (board.tryMove(currentShape, 0, 1)) {
                    score += 1
                    isLocking = false
                    moved = true
                }
            }
            PlayerAction.ROTATE     -> moved = board.tryRotate(currentShape)
            PlayerAction.QUIT       -> {
                isRunning = false
                return
            }
        }

        if (moved && isLocking) {
            lockTimer = System.currentTimeMillis()
        }

        draw()
    }

    private fun lockAndSpawn() {
        board.lockPiece(currentShape)
        val cleared = board.clearFullLines()

        if (cleared > 0) {
            val points = when (cleared) {
                1 -> 100
                2 -> 300
                3 -> 500
                4 -> 800
                else -> 0
            }
            score += points * level
            level = (score / 1000) + 1
            fallDelay = (500L - (level - 1) * 50L).coerceAtLeast(100L)
        }

        currentShape = nextShape
        nextShape = board.spawnShape()

        isLocking = false
        lockTimer = 0L

        if (!board.isValidPosition(currentShape)) {
            gameOver()
        }
    }

    private fun draw() {
        if (isRunning) {
            renderer.renderGame(board, currentShape, nextShape, score, level)
        }
    }

    private fun gameOver() {
        isRunning = false
        renderer.renderGameOver(score, level)
    }
}