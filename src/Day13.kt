import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.lang.Integer.min

fun main() {


    checkSolution("Day 13 Part 1", day13Part1Solution("inputs/day13/example.txt"))
    checkSolution("Day 13 Part 1", day13Part1Solution("inputs/day13/part1.txt"))
}

class RockLine(var pattern: String, val index: Int) {

}

class Terrain() {
    val rows = mutableListOf<RockLine>()
    val cols = mutableListOf<RockLine>()

    fun addRow(pattern: String) {
        rows.add(RockLine(pattern, rows.size + 1))
        if(cols.size == 0) {
            pattern.forEachIndexed { index, char -> cols.add(RockLine(char.toString(), index + 1)) }
        }
        else {
            pattern.forEachIndexed { index, char -> cols[index].pattern += char }
        }
    }

    fun getReflectionScore(): Long {

        // Iterate through rows looking for two that are next to each other that are the same then spider out from there.
        return 0.toLong()
    }
}
fun day13Part1Solution(path: String): Long {
    return 0.toLong()
}


fun day13Part2Solution(path: String): Long {
    return 0.toLong()
}
