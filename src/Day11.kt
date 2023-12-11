import java.io.File

fun main() {
    checkSolution("Day 11 Part 1 Example", day11Part1Solution("inputs/day11/example.txt"), 374)
    checkSolution("Day 11 Part 1", day11Part1Solution("inputs/day11/part1.txt"), 9545480)
    checkSolution("Day 10 Part 2 Example", day11Part2Solution("inputs/day11/example.txt", 2), 374)
    checkSolution("Day 10 Part 2 Example", day11Part2Solution("inputs/day11/example.txt", 100), 8410)
    checkSolution("Day 10 Part 2", day11Part2Solution("inputs/day11/part1.txt", 1000000))
}

class Galaxy(val row: Long, val col: Long) {
    fun distance(other: Galaxy): Long {
        return Math.abs(other.row - row) + Math.abs(other.col - col)
    }
}

fun day11Part1Solution(path: String): Long {

    val universe = File(path).readLines().map {row ->
        row.map { it == '#'}.toMutableList()
    }.toMutableList()

    var row = 0
    while(row < universe.size) {
        if(!universe[row].any {it }) {
            universe.add(row, universe[row].toMutableList())
            row++
        }
        row++
    }
    var col = 0
    while(col < universe[0].size) {
        var colHasGalaxy = false
        for(j in universe.indices) {
            if(universe[j][col]) {
                colHasGalaxy = true
                break
            }
        }
        if(!colHasGalaxy) {
            for(j in universe.indices) {
                universe[j].add(col, false)
            }
            col++
        }
        col++
    }
    val galaxies = mutableListOf<Galaxy>()
    universe.forEachIndexed { rowIndex, locationRow -> locationRow.forEachIndexed { colIndex, location -> if(location) galaxies.add(Galaxy(
        rowIndex.toLong(), colIndex.toLong()
    )) }}

    var sum = 0.toLong()
    for(i in 0 until galaxies.lastIndex) {
        for(j in i + 1 until galaxies.size) {
            sum += galaxies[i].distance(galaxies[j])
        }
    }
    return sum

}

fun day11Part2Solution(path: String, expansionSize: Long): Long {
    val universe = File(path).readLines().map {row ->
        row.map { it == '#'}.toMutableList()
    }.toMutableList()

    val emptyRows = mutableListOf<Int>()
    for(i in universe.indices) {
        if(!universe[i].any { it }) {
            emptyRows.add(i)
        }
    }

    val emptyCols = mutableListOf<Int>()
    for(j in 0 until universe[0].size) {
        var colHasGalaxy = false
        for(i in universe.indices) {
            if(universe[i][j]) {
                colHasGalaxy = true
                break
            }
        }
        if(!colHasGalaxy) {
            emptyCols.add(j)
        }
    }

    val galaxies = mutableListOf<Galaxy>()
    var realRowIndex = 0.toLong()
    for(i in universe.indices) {
        if(i in emptyRows) {
            realRowIndex += expansionSize
            continue
        }
        var realColIndex = 0.toLong()
        for(j in universe[i].indices) {
            if(j in emptyCols) {
                realColIndex += expansionSize
                continue
            }
            if(universe[i][j]) {
                galaxies.add(Galaxy(realRowIndex, realColIndex))
            }
            realColIndex++
        }
        realRowIndex++
    }


    var sum = 0.toLong()
    for(i in 0 until galaxies.lastIndex) {
        for(j in i + 1 until galaxies.size) {
            sum += galaxies[i].distance(galaxies[j])
        }
    }
    return sum
}
