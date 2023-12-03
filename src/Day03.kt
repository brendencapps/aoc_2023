import java.io.File

fun main() {
    checkSolution("Day 3 Part 1 Example", day3Part1Solution("inputs/day03/example.txt"), 4361)
    checkSolution("Day 3 Part 1", day3Part1Solution("inputs/day03/part1.txt"), 554003)
    checkSolution("Day 3 Part 2 Example", day3Part2Solution("inputs/day03/example.txt"), 467835)
    checkSolution("Day 3 Part 2", day3Part2Solution("inputs/day03/part1.txt"), 87263515)
}

data class Symbol(val symbol: Char, val row: Int, val col: Int) {
    fun nextToPartNumber(part: PartNumber): Boolean {
        return (col in part.col - part.number.length - 1..part.col) && (row in part.row - 1..part.row + 1)
    }
}
data class PartNumber(val number: String, val row: Int, val col: Int )

data class ProblemInput(val path: String, val parts: MutableList<PartNumber> = mutableListOf(), val symbols: MutableList<Symbol> = mutableListOf()) {
    init {
        File(path).readLines().forEachIndexed { row, value ->
            var currentNumber: String? = null
            value.forEachIndexed { col, ch ->
                if (ch.isDigit()) {
                    if (currentNumber == null) {
                        currentNumber = ""
                    }
                    currentNumber += ch
                } else {
                    if (ch != '.') {
                        symbols.add(Symbol(ch, row, col))
                    }
                    if (currentNumber != null) {
                        parts.add(PartNumber(currentNumber!!, row, col))
                        currentNumber = null
                    }
                }
            }
            if (currentNumber != null) {
                parts.add(PartNumber(currentNumber!!, row, value.length))
                currentNumber = null
            }
        }
    }
}

fun day3Part1Solution(path: String): Int {
    val problemInput = ProblemInput(path)
    return problemInput.parts.sumOf { number ->
        if(problemInput.symbols.any { it.nextToPartNumber(number) }) {
            number.number.toInt()
        }
        else {
            0
        }
    }
}

fun day3Part2Solution(path: String): Int {
    val problemInput = ProblemInput(path)
    return problemInput.symbols.sumOf { symbol ->
        if(symbol.symbol == '*') {
            val adjacentNumbers = problemInput.parts.filter { symbol.nextToPartNumber(it) }
            if(adjacentNumbers.size == 2) {
                adjacentNumbers[0].number.toInt() * adjacentNumbers[1].number.toInt()
            }
            else {
                0
            }
        }
        else {
            0
        }
    }
}
