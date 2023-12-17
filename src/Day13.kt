import java.io.File
import java.lang.Integer.min

fun main() {


    checkSolution("Day 13 Part 1", day13PartSolution("inputs/day13/example.txt", 0), 405)
    checkSolution("Day 13 Part 1", day13PartSolution("inputs/day13/part1.txt", 0), 34918)
    checkSolution("Day 13 Part 2", day13PartSolution("inputs/day13/example.txt", 1), 400)
    checkSolution("Day 13 Part 2", day13PartSolution("inputs/day13/part1.txt", 1), 33054)
}


class Terrain {
    private val rows = mutableListOf<String>()
    private val cols = mutableListOf<String>()

    fun addRow(pattern: String) {
        rows.add(pattern)
        if(cols.size == 0) {
            pattern.forEach { char -> cols.add(char.toString()) }
        }
        else {
            pattern.forEachIndexed { index, char -> cols[index] = cols[index] + char }
        }
    }

    fun getReflectionScore(smudgesAllowed: Int = 0): Long {

        // Iterate through rows looking for two that are next to each other that are the same then spider out from there.
        return (findReflectionPoint(cols, smudgesAllowed) + findReflectionPoint(rows, smudgesAllowed) * 100).toLong()
    }

    private fun findReflectionPoint(terrain: List<String>, smudgesAllowed: Int): Int {
        for(i in 1 until terrain.size) {
            if(isReflectionPointWithSmudge(i, terrain, smudgesAllowed)) {
                return i
            }
        }
        return 0
    }

    private fun isReflectionPointWithSmudge(index: Int, terrain: List<String>, smudgesAllowed: Int): Boolean {
        val rowsToCheck = min(index, terrain.size - index)
        var smudges = 0
        for(i in 0 until rowsToCheck) {
            val leftTerrainIndex = index - i - 1
            val rightTerrainIndex = index + i
            if(terrain[leftTerrainIndex] != terrain[rightTerrainIndex]) {
                smudges += smudges(terrain[leftTerrainIndex], terrain[rightTerrainIndex])
                if(smudges > smudgesAllowed) {
                    return false
                }
            }
        }
        return smudges == smudgesAllowed
    }

    private fun smudges(terrain1: String, terrain2: String): Int {
        var smudges = 0
        for(i in terrain1.indices) {
            if(terrain1[i] != terrain2[i]) {
                smudges++
            }
        }
        return smudges
    }
}
fun day13PartSolution(path: String, smudgesAllowed: Int): Long {
    var terrain = Terrain()
    val terrains = mutableListOf(terrain)
    File(path).readLines().forEach { line ->
        if(line.isBlank()) {
            terrain = Terrain()
            terrains.add(terrain)
        }
        else {
            terrain.addRow(line)
        }
    }
    return terrains.sumOf { it.getReflectionScore(smudgesAllowed) }
}
