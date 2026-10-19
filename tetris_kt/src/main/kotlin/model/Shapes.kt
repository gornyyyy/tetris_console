package org.example.model

// (Палка) - Cyan
class ShapeI(x: Int, y: Int) : Shape(x, y) {
    init {
        matrix = arrayOf(
            intArrayOf(1, 1, 1, 1)
        )
    }
    override fun rotate() {
        rotateMatrixClockwise()
    }
}

// (Квадрат) - Yellow
class ShapeO(x: Int, y: Int) : Shape(x, y) {
    init {
        matrix = arrayOf(
            intArrayOf(1, 1),
            intArrayOf(1, 1)
        )
    }
    override fun rotate() {
    }
}

// 3. T-фигура - Purple
class ShapeT(x: Int, y: Int) : Shape(x, y) {
    init {
        matrix = arrayOf(
            intArrayOf(0, 1, 0),
            intArrayOf(1, 1, 1)
        )
    }
    override fun rotate() {
        rotateMatrixClockwise()
    }
}

// S-фигура - Green
class ShapeS(x: Int, y: Int) : Shape(x, y) {
    init {
        matrix = arrayOf(
            intArrayOf(0, 1, 1),
            intArrayOf(1, 1, 0)
        )
    }
    override fun rotate() {
        rotateMatrixClockwise()
    }
}

// Z-фигура - Red
class ShapeZ(x: Int, y: Int) : Shape(x, y) {
    init {
        matrix = arrayOf(
            intArrayOf(1, 1, 0),
            intArrayOf(0, 1, 1)
        )
    }
    override fun rotate() {
        rotateMatrixClockwise()
    }
}

// J-фигура - Blue
class ShapeJ(x: Int, y: Int) : Shape(x, y) {
    init {
        matrix = arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(1, 1, 1)
        )
    }
    override fun rotate() {
        rotateMatrixClockwise()
    }
}

// L-фигура - Orange
class ShapeL(x: Int, y: Int) : Shape(x, y) {
    init {
        matrix = arrayOf(
            intArrayOf(0, 0, 1),
            intArrayOf(1, 1, 1)
        )
    }
    override fun rotate() {
        rotateMatrixClockwise()
    }
}