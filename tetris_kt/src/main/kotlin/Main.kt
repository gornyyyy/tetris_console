package org.example

import kotlin.concurrent.thread
import kotlin.system.exitProcess

fun main() {
    runCatching {
        ProcessBuilder("stty", "-icanon", "min", "1")
            .inheritIO()
            .start()
            .waitFor()
    }

    try {
        val game = Game()
        game.start()

        System.`in`.read()

        thread(isDaemon = true) {
            while (game.isRunning) {
                Thread.sleep(game.fallDelay)
                if (!game.isRunning) break
                game.tick()
            }
        }

        while (game.isRunning) {
            val input = System.`in`.read()
            if (input == -1) break // EOF

            val action = when (input.toChar().lowercaseChar()) {
                'a' -> PlayerAction.MOVE_LEFT
                'd' -> PlayerAction.MOVE_RIGHT
                's' -> PlayerAction.MOVE_DOWN
                'w', 'r' -> PlayerAction.ROTATE
                'q' -> PlayerAction.QUIT
                else -> null
            }

            action?.let { game.handleInput(it) }
        }
    } finally {
        runCatching {
            ProcessBuilder("stty", "sane")
                .inheritIO()
                .start()
                .waitFor()
        }
    }

    exitProcess(0)
}