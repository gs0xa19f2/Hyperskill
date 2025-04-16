package minesweeper

fun main() {
    println("How many mines do you want on the field?")
    val minesCount = readlnOrNull()?.toIntOrNull() ?: 0
    val rows = 9
    val cols = 9

    var minesGenerated = false
    var finalField = Array(rows) { CharArray(cols) { '.' } }
    val displayedField = Array(rows) { CharArray(cols) { '.' } }

    fun countMinesAround(r: Int, c: Int): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until rows && nc in 0 until cols && finalField[nr][nc] == 'X') {
                    count++
                }
            }
        }
        return count
    }

    fun generateMines(firstRow: Int, firstCol: Int) {
        finalField = Array(rows) { CharArray(cols) { '.' } }
        val availableCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (r != firstRow || c != firstCol) availableCells.add(Pair(r, c))
            }
        }
        val shuffled = availableCells.shuffled().take(minesCount)
        shuffled.forEach { (r, c) -> finalField[r][c] = 'X' }
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (finalField[r][c] != 'X') {
                    val cnt = countMinesAround(r, c)
                    finalField[r][c] = if (cnt > 0) cnt.digitToChar() else '.'
                }
            }
        }
    }

    fun exploreCell(r: Int, c: Int) {
        if (r !in 0 until rows || c !in 0 until cols) return
        if (displayedField[r][c] == '/' || displayedField[r][c].isDigit()) return

        if (displayedField[r][c] == '*') displayedField[r][c] = '.'

        when (finalField[r][c]) {
            'X' -> return
            '.' -> {
                displayedField[r][c] = '/'
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        exploreCell(r + dr, c + dc)
                    }
                }
            }
            else -> displayedField[r][c] = finalField[r][c]
        }
    }

    fun checkWin(): Boolean {
        var allMinesMarked = true
        var noWrongMarks = true
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (finalField[r][c] == 'X' && displayedField[r][c] != '*') allMinesMarked = false
                if (finalField[r][c] != 'X' && displayedField[r][c] == '*') noWrongMarks = false
            }
        }
        if (allMinesMarked && noWrongMarks) return true

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (finalField[r][c] != 'X' && (displayedField[r][c] == '.' || displayedField[r][c] == '*')) return false
            }
        }
        return true
    }

    fun printField() {
        println(" │123456789│")
        println("—│—————————│")
        for (r in 0 until rows) {
            print("${r + 1}│")
            print(displayedField[r].joinToString(""))
            println("│")
        }
        println("—│—————————│")
    }

    printField()

    while (true) {
        println("Set/unset mine marks or claim a cell as free:")
        val input = readlnOrNull()?.split(" ") ?: continue
        if (input.size != 3) continue

        val x = input[0].toIntOrNull() ?: continue
        val y = input[1].toIntOrNull() ?: continue
        val cmd = input[2]

        if (x !in 1..cols || y !in 1..rows || cmd !in listOf("mine", "free")) continue

        val c = x - 1
        val r = y - 1

        when (cmd) {
            "mine" -> {
                if (displayedField[r][c] == '.') {
                    displayedField[r][c] = '*'
                } else if (displayedField[r][c] == '*') {
                    displayedField[r][c] = '.'
                }
            }
            "free" -> {
                if (!minesGenerated) {
                    generateMines(r, c)
                    minesGenerated = true
                }
                if (finalField[r][c] == 'X') {
                    for (i in 0 until rows) {
                        for (j in 0 until cols) {
                            if (finalField[i][j] == 'X') displayedField[i][j] = 'X'
                        }
                    }
                    printField()
                    println("You stepped on a mine and failed!")
                    return
                }
                exploreCell(r, c)
            }
        }

        printField()

        if (checkWin()) {
            println("Congratulations! You found all the mines!")
            return
        }
    }
}