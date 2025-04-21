package minesweeper

// Константы для размеров игрового поля и обозначений
const val ROWS = 9
const val COLS = 9
const val EMPTY_CELL = '.'
const val MINE = 'X'
const val MARK = '*'
const val EXPLORED = '/'

fun main() {
    println("How many mines do you want on the field?")
    val minesCount = readlnOrNull()?.toIntOrNull() ?: 0

    var minesGenerated = false
    var finalField = Array(ROWS) { CharArray(COLS) { EMPTY_CELL } }
    val displayedField = Array(ROWS) { CharArray(COLS) { EMPTY_CELL } }

    // Проверка, находится ли ячейка в пределах игрового поля
    fun isInBounds(r: Int, c: Int): Boolean {
        return r in 0 until ROWS && c in 0 until COLS
    }

    // Подсчет количества мин вокруг заданной ячейки
    fun countMinesAround(r: Int, c: Int): Int {
        var count = 0
        for (dr in -1..1) {
            for (dc in -1..1) {
                if (dr == 0 && dc == 0) continue
                val nr = r + dr
                val nc = c + dc
                if (isInBounds(nr, nc) && finalField[nr][nc] == MINE) {
                    count++
                }
            }
        }
        return count
    }

    // Генерация мин на поле, исключая первую выбранную ячейку
    fun generateMines(firstRow: Int, firstCol: Int) {
        finalField = Array(ROWS) { CharArray(COLS) { EMPTY_CELL } }
        val availableCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                if (r != firstRow || c != firstCol) availableCells.add(Pair(r, c))
            }
        }
        val shuffled = availableCells.shuffled().take(minesCount)
        shuffled.forEach { (r, c) -> finalField[r][c] = MINE }
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                if (finalField[r][c] != MINE) {
                    val cnt = countMinesAround(r, c)
                    finalField[r][c] = if (cnt > 0) cnt.digitToChar() else EMPTY_CELL
                }
            }
        }
    }

    // Исследование ячейки
    fun exploreCell(r: Int, c: Int) {
        if (!isInBounds(r, c)) return
        if (displayedField[r][c] == EXPLORED || displayedField[r][c].isDigit()) return

        if (displayedField[r][c] == MARK) displayedField[r][c] = EMPTY_CELL

        when (finalField[r][c]) {
            MINE -> return
            EMPTY_CELL -> {
                displayedField[r][c] = EXPLORED
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        exploreCell(r + dr, c + dc)
                    }
                }
            }
            else -> displayedField[r][c] = finalField[r][c]
        }
    }

    // Проверка условий победы
    fun checkWin(): Boolean {
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                if (finalField[r][c] == MINE && displayedField[r][c] != MARK) return false
                if (finalField[r][c] != MINE && displayedField[r][c] in listOf(EMPTY_CELL, MARK)) return false
            }
        }
        return true
    }

    // Вывод текущего состояния игрового поля
    fun printField() {
        println(" │123456789│")
        println("—│—————————│")
        for (r in 0 until ROWS) {
            print("${r + 1}│")
            print(displayedField[r].joinToString(""))
            println("│")
        }
        println("—│—————————│")
    }

    // Обработка ввода пользователя
    fun parseInput(input: String?): Triple<Int, Int, String>? {
        val parts = input?.split(" ") ?: return null
        if (parts.size != 3) return null
        val x = parts[0].toIntOrNull() ?: return null
        val y = parts[1].toIntOrNull() ?: return null
        val cmd = parts[2]
        return if (x in 1..COLS && y in 1..ROWS && cmd in listOf("mine", "free")) Triple(x - 1, y - 1, cmd) else null
    }

    printField()

    // Основной игровой цикл
    while (true) {
        println("Set/unset mine marks or claim a cell as free:")
        val (c, r, cmd) = parseInput(readlnOrNull()) ?: continue

        when (cmd) {
            "mine" -> {
                if (displayedField[r][c] == EMPTY_CELL) {
                    displayedField[r][c] = MARK
                } else if (displayedField[r][c] == MARK) {
                    displayedField[r][c] = EMPTY_CELL
                }
            }
            "free" -> {
                if (!minesGenerated) {
                    generateMines(r, c)
                    minesGenerated = true
                }
                if (finalField[r][c] == MINE) {
                    for (i in 0 until ROWS) {
                        for (j in 0 until COLS) {
                            if (finalField[i][j] == MINE) displayedField[i][j] = MINE
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
