import java.io.File

fun main() {
    checkSolution("Day 9 Part 1 Example", day9Part1Solution("inputs/day09/example.txt"), 114)
    checkSolution("Day 9 Part 1", day9Part1Solution("inputs/day09/part1.txt"), 1684566095)
    checkSolution("Day 9 Part 2 Example", day9Part2Solution("inputs/day09/example.txt"), 2)
    checkSolution("Day 9 Part 2", day9Part2Solution("inputs/day09/part1.txt"))
}

fun day9Part1Solution(path: String): Long {

    return File(path).readLines().sumOf {line ->
        var result = 0.toLong()
        val row = line.split(" ").map { it.toLong() }.toMutableList()
        result += row[row.size - 1]
        while(row.any { it != 0.toLong() }) {
            val nextRow = mutableListOf<Long>()
            for(i in 1 until row.size) {
                nextRow.add(row[i] - row[i-1])
            }
            result += nextRow.last()
            row.clear()
            row.addAll(nextRow)
        }
        result
    }
}

fun day9Part2Solution(path: String): Long {

    return File(path).readLines().sumOf {line ->
        val listOfNumbers = mutableListOf<Long>()
        val row = line.split(" ").map { it.toLong() }.toMutableList()
        listOfNumbers.add(row.first())
        while(row.any { it != 0.toLong() }) {
            val nextRow = mutableListOf<Long>()
            for(i in 1 until row.size) {
                nextRow.add(row[i] - row[i-1])
            }
            listOfNumbers.add(nextRow.first())
            row.clear()
            row.addAll(nextRow)
        }
        var currentNumber = 0.toLong()
        for(i in listOfNumbers.indices) {
            currentNumber = listOfNumbers[listOfNumbers.size - 1 - i] - currentNumber
        }
        currentNumber
    }
}
