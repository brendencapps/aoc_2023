import java.io.File
import javax.swing.Spring

fun main() {

    checkSolution("Example Row 1", getCombinations(getSpringRow("???.###"), getBrokenPattern("1,1,3")), 1)

    checkSolution("Example Row 2", getCombinations(getSpringRow(".??..??...?##."), getBrokenPattern("1,1,3")), 4)

    checkSolution("Example Row 3", getCombinations(getSpringRow("?#?#?#?#?#?#?#?"), getBrokenPattern("1,3,1,6")), 1)
    checkSolution("Example Row 4", getCombinations(getSpringRow("????.#...#..."), getBrokenPattern("4,1,1")), 1)
    checkSolution("Example Row 5", getCombinations(getSpringRow("????.######..#####."), getBrokenPattern("1,6,5")), 4)
    checkSolution("Example Row 5", getCombinations(getSpringRow("?###????????"), getBrokenPattern("3,2,1")), 10)

    checkSolution("Day 12 Part 1 Example", day12Part1Solution("inputs/day12/example.txt"), 21)
    //checkSolution("Day 12 Part 1", day12Part1Solution("inputs/day12/part1.txt"))
    //checkSolution("Day 12 Part 2 Example", day12Part2Solution("inputs/day12/example.txt", 2), 374)
    //checkSolution("Day 12 Part 2 Example", day12Part2Solution("inputs/day12/example.txt", 100), 8410)
    //checkSolution("Day 12 Part 2", day12Part2Solution("inputs/day12/part1.txt", 1000000))
}

enum class SpringCondition {
    Working, Broken, Unknown
}

fun getConditionsFromPattern(brokenSize: List<Int>, brokenStart: List<Int>, rowSize: Int): List<SpringCondition> {
    val springs = mutableListOf<SpringCondition>()
    for(i in 0 until brokenStart[0]) {
        springs.add(SpringCondition.Working)
    }
}

fun getCombinations(springs: List<SpringCondition>, broken: List<Int>): Int {
    var currentPos = 0
    val startPos = broken.map { brokenSize ->
        val pos = currentPos
        currentPos += brokenSize + 1
        pos
    }



    return 0
}

fun getSpringRow(conditions: String): List<SpringCondition> {
    return conditions.map { when(it) {
        '?' -> SpringCondition.Unknown
        '#' -> SpringCondition.Broken
        else -> SpringCondition.Working
    } }
}

fun getBrokenPattern(pattern: String): List<Int> {
    return pattern.split(",").map { it.toInt() }
}

fun day12Part1Solution(path: String): Long {

    return File(path).readLines().sumOf { springRow ->
        val parts = springRow.split(" ")
        getCombinations(getSpringRow(parts[0]), getBrokenPattern(parts[1]))
    }.toLong()

}

fun day12Part2Solution(path: String, expansionSize: Long): Long {
    return 0.toLong()
}
