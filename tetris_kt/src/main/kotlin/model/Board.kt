package org.example.model

class Board {
    companion object {
        const val WIDTH = 10
        const val VISIBLE_HEIGHT = 20
        const val HIDDEN_ROWS = 4
        const val TOTAL_HEIGHT = VISIBLE_HEIGHT + HIDDEN_ROWS // 24
    }

    val grid: Array<IntArray> = Array(TOTAL_HEIGHT) { IntArray(WIDTH) }

    init {
        for (c in 0 until WIDTH) {
            grid[TOTAL_HEIGHT - 1][c] = 3
        }
    }

    fun isValidPosition(shape: Shape): Boolean {
        for (block in shape.getBlocks()) {
            if (block.x < 0 || block.x >= WIDTH) return false

            if (block.y >= TOTAL_HEIGHT) return false
            if (block.y >= 0 && grid[block.y][block.x] != 0) return false
        }
        return true
    }

    fun tryMove(shape: Shape, dx: Int, dy: Int): Boolean {
        shape.move(dx, dy)
        if (isValidPosition(shape)) {
            return true
        }
        shape.move(-dx, -dy)
        return false
    }


    fun tryRotate(shape: Shape): Boolean {
        val backupMatrix = Array(shape.matrix.size) { r -> shape.matrix[r].clone() }

        shape.rotate()

        if (isValidPosition(shape)) {
            return true
        } else {
            shape.matrix = backupMatrix
            return false
        }
    }

    fun lockPiece(shape: Shape) {
        for (block in shape.getBlocks()) {
            if (block.y >= 0 && block.y < TOTAL_HEIGHT && block.x >= 0 && block.x < WIDTH) {
                grid[block.y][block.x] = 2
            }
        }
    }

    fun spawnShape(): Shape {
        val shape = when ((0..6).random()) {
            0 -> ShapeI(3, 0)
            1 -> ShapeO(4, 0)
            2 -> ShapeT(3, 0)
            3 -> ShapeS(3, 0)
            4 -> ShapeZ(3, 0)
            5 -> ShapeJ(3, 0)
            else -> ShapeL(3, 0)
        }
        return shape
    }

    fun clearFullLines(): Int {
        var linesCleared = 0

        for (r in TOTAL_HEIGHT - 2 downTo 0) {
            val isFull = grid[r].all { it != 0 }

            if (isFull) {
                linesCleared++
                for (row in r downTo 1) {
                    grid[row] = grid[row - 1].clone()
                }
                grid[0] = IntArray(WIDTH)
            }
        }

        return linesCleared
    }

}