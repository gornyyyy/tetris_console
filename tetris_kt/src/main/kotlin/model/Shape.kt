package org.example.model

abstract class Shape(
    var x: Int, var y: Int
) {
    var matrix: Array<IntArray> = emptyArray()

    abstract fun rotate()

    fun move(dx: Int, dy: Int) {
        x += dx
        y += dy
    }

    fun moveLeft() = move(-1, 0)
    fun moveRight() = move(1, 0)

    open fun moveDown() = move(0, 1)

    protected fun rotateMatrixClockwise() {
        val rows = matrix.size
        val cols = matrix[0].size
        val rotated = Array(cols) { IntArray(rows) }
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                rotated[c][rows - 1 - r] = matrix[r][c]
            }
        }
        matrix = rotated
    }

    fun getBlocks(): List<Point> {
        val blocks = mutableListOf<Point>()
        for (r in matrix.indices) {
            for (c in matrix[r].indices) {
                if (matrix[r][c] == 1) blocks.add(Point(x + c, y + r))
            }
        }
        return blocks
    }
}

data class Point(val x: Int, val y: Int)