import java.io.File

fun main() {
    checkSolution("Day 15 Part 1 Example", day15Part1Solution("inputs/day15/example.txt"), 1320)
    checkSolution("Day 15 Part 1", day15Part1Solution("inputs/day15/part1.txt"),516804)
    checkSolution("Day 15 Part 2 Example", day15Part2Solution("inputs/day15/example.txt"), 145)
    checkSolution("Day 15 Part 2 Example", day15Part2Solution("inputs/day15/part1.txt"))
}

fun day15Part1Solution(path: String): Long {
    return File(path).readLines().sumOf { line ->
        line.split(",").sumOf { text ->
            day15Hash(text).toLong()
        }
    }
}

fun day15Hash(lens: String): Int {
    var hash = 0
    lens.forEach {
        hash += it.code
        hash *= 17
        hash %= 256
    }
    return hash
}

class Day15Lens(input: String) {
    val operationAdd = input.contains('=')
    private val lensParts = if(operationAdd) { input.split("=") } else { input.split('-') }
    val lensHash = day15Hash(lensParts[0])
    val lensName = lensParts[0]
    var focalLength = if(operationAdd) { lensParts[1].toInt() } else { 0 }
}


fun day15Part2Solution(path: String): Long {
    val lensBoxes = List<MutableList<Day15Lens>>(256) { mutableListOf() }
    File(path).readLines().forEach { line ->
        line.split(",").forEach { lensInput ->
            val lens = Day15Lens(lensInput)
            if(lens.operationAdd) {
                val lensInBox = lensBoxes[lens.lensHash].firstOrNull { it.lensName == lens.lensName}
                if(lensInBox != null) {
                    lensInBox.focalLength = lens.focalLength
                }
                else {
                    lensBoxes[lens.lensHash].add(lens)
                }
            }
            else {
                lensBoxes[lens.lensHash].removeIf {
                    it.lensName == lens.lensName
                }
            }
        }
    }

    return lensBoxes.mapIndexed { boxIndex, box ->
        box.mapIndexed { slot, lens ->
            ((boxIndex + 1) * (slot + 1) * lens.focalLength).toLong()
        }.sum()
    }.sum()

}