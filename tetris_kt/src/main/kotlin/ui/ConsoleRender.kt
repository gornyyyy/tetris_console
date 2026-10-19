package org.example.ui

import org.example.model.Board
import org.example.model.Shape

class ConsoleRenderer {

    // ANSI-код для очистки экрана
    private val clearScreen = "\u001b[H\u001b[2J"

    // Метод для старта игры
    fun renderStartScreen() {
        print(clearScreen)
        println("=======================")
        println("  Welcome to TETRIS!   ")
        println("=======================")
        println("Controls:")
        println("  [A] - Move Left")
        println("  [D] - Move Right")
        println("  [S] - Soft Drop")
        println("  [W] - Rotate")
        println("  [Q] - Quit Game")
        println("=======================")
        println("Press Enter to start...")
    }

    // Основной метод отрисовки игрового процесса
    fun renderGame(board: Board, currentShape: Shape, nextShape: Shape, score: Int, level: Int) {
        // 1. Копируем видимую часть поля, чтобы наложить на нее активную фигуру
        val display = Array(Board.TOTAL_HEIGHT) { r -> board.grid[r].clone() }

        // 2. Накладываем активную фигуру на копию поля
        for (block in currentShape.getBlocks()) {
            if (block.y >= 0 && block.y < Board.TOTAL_HEIGHT && block.x in 0 until Board.WIDTH) {
                display[block.y][block.x] = 1 // 1 = активная фигура
            }
        }

        // 3. Очищаем экран
        print(clearScreen)

        // 4. Выводим статистику и следующую фигуру
        println("  SCORE: $score  |  LEVEL: $level")
        println("  NEXT PIECE:")
        printNextShape(nextShape)

        // 5. Рисуем верхнюю границу
        println("+" + "-".repeat(Board.WIDTH * 2) + "+")

        // 6. Рисуем только видимые строки (пропускаем скрытые 4)
        for (r in Board.HIDDEN_ROWS until Board.TOTAL_HEIGHT) {
            print("|")
            for (c in 0 until Board.WIDTH) {
                when (display[r][c]) {
                    0 -> print(" .") // Пусто
                    1 -> print(" #") // Активная фигура
                    2 -> print("[]") // Упавший блок
                    3 -> print("==") // Дно/стена
                }
            }
            println("|")
        }

        // 7. Рисуем нижнюю границу и подсказки
        println("+" + "-".repeat(Board.WIDTH * 2) + "+")
        println("Controls: [A] Left, [D] Right, [S] Down, [W] Rotate, [Q] Quit")
    }

    // Метод для экрана Game Over
    fun renderGameOver(score: Int, level: Int) {
        println("\n=======================")
        println("       GAME OVER       ")
        println("=======================")
        println("  Final Score: $score")
        println("  Final Level: $level")
        println("=======================")
        println("Thanks for playing!")
    }

    // Вспомогательный метод для отрисовки следующей фигуры
    private fun printNextShape(shape: Shape) {
        val matrix = shape.matrix
        for (row in matrix) {
            print("  ")
            for (cell in row) {
                if (cell == 1) print("[]") else print("  ")
            }
            println()
        }
    }
}