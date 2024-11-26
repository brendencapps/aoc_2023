import java.io.File
import kotlin.math.abs

fun main() {
    checkSolution("Day 18 Part 1 Example", day18Part1Solution("inputs/day18/example.txt"), 62)
    checkSolution("Day 18 Part 1", day18Part1Solution("inputs/day18/part1.txt"), 38188)
    checkSolution("Day 18 Part 2 Example", day18Part2Solution("inputs/day18/example.txt"), 952408144115)
    checkSolution("Day 18 Part 2 Example", day18Part2Solution("inputs/day18/part1.txt"), 93325849869340)
}

data class Instruction(val direction: String, var length: Long, val color: Int)
fun part1InstructionParser(instruction: String): Instruction {
    val regex = "(\\S) (\\d*) \\(#(\\S*)\\)".toRegex()
    val (direction, length, color) = regex.find(instruction)?.destructured ?: error("Error parsing instruction $instruction")
    return Instruction(direction, length.toLong(), color.toInt(16))
}

data class Day18Vector(val x: Long, val y: Long) {
    fun offset(other: Day18Vector): Day18Vector {
        return Day18Vector(x + other.x, y + other.y)
    }
}


fun part2InstructionParser(instruction: String): Instruction {
    val regex = "(\\S) (\\d*) \\(#(\\S*)\\)".toRegex()
    val (_, _, color) = regex.find(instruction)?.destructured ?: error("Error parsing instruction $instruction")
    val lengthFromColor = color.substring(0, color.length - 1).toInt(16)
    val directionFromColor = when(color.substring(color.length - 1, color.length)) {
        "0" -> "R"
        "1" -> "D"
        "2" -> "L"
        else -> "U"
    }
    return Instruction(directionFromColor, lengthFromColor.toLong(), 0)
}

class Day18(private val input: String) {

    fun getCubicMeters(instructionParser: (String) -> Instruction): Long {

        val vectors = File(input).readLines().map { line ->
            instructionParser(line)
        }.map { instruction ->
            when (instruction.direction) {
                "R" -> Day18Vector(instruction.length, 0)
                "L" -> Day18Vector(-instruction.length, 0)
                "U" -> Day18Vector(0, -instruction.length)
                else -> Day18Vector(0, instruction.length)
            }
        }
        val perimeter = vectors.sumOf { abs(it.x) + abs(it.y) }
        val shape = vectors.scan(Day18Vector(0, 0)) { acc, point ->
            acc.offset(point)
        }
        val area = shape.zipWithNext {a, b ->
            a.x * b.y - a.y * b.x
        }.sum() / 2
        return area + perimeter / 2L + 1L
    }

}

fun day18Part1Solution(path: String): Long {
    return Day18(path).getCubicMeters(::part1InstructionParser)
}


fun day18Part2Solution(path: String): Long {
    return Day18(path).getCubicMeters(::part2InstructionParser)
}