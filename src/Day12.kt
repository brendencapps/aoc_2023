import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.lang.Integer.min

fun main() {

/*
    checkSolution("Example Row 1", Day12Springs(getSpringRow("???.###"), getBrokenPattern("1,1,3")).getCombinations(), 1)
    checkSolution("Example Row 2", Day12Springs(getSpringRow(".??..??...?##."), getBrokenPattern("1,1,3")).getCombinations(), 4)
    checkSolution("Example Row 3", Day12Springs(getSpringRow("?#?#?#?#?#?#?#?"), getBrokenPattern("1,3,1,6")).getCombinations(), 1)
    checkSolution("Example Row 4", Day12Springs(getSpringRow("????.#...#..."), getBrokenPattern("4,1,1")).getCombinations(), 1)
    checkSolution("Example Row 5", Day12Springs(getSpringRow("????.######..#####."), getBrokenPattern("1,6,5")).getCombinations(), 4)
    checkSolution("Example Row 5", Day12Springs(getSpringRow("?###????????"), getBrokenPattern("3,2,1")).getCombinations(), 10)
    checkSolution("Day 12 Part 1 Example", day12Part1Solution("inputs/day12/example.txt"), 21)
    checkSolution("Day 12 Part 1", day12Part1Solution("inputs/day12/part1.txt"), 7402)

    checkSolution("Example Row 1", Day12Springs(getSpringRow2("???.###"), getBrokenPatternPart2("1,1,3")).getCombinations(), 1)
    checkSolution("Example Row 2", Day12Springs(getSpringRow2(".??..??...?##."), getBrokenPatternPart2("1,1,3")).getCombinations(), 16384)
    checkSolution("Example Row 3", Day12Springs(getSpringRow2("?#?#?#?#?#?#?#?"), getBrokenPatternPart2("1,3,1,6")).getCombinations(), 1)
    checkSolution("Example Row 4", Day12Springs(getSpringRow2("????.#...#..."), getBrokenPatternPart2("4,1,1")).getCombinations(), 16)
    checkSolution("Example Row 5", Day12Springs(getSpringRow2("????.######..#####."), getBrokenPatternPart2("1,6,5")).getCombinations(), 2500)
    checkSolution("Example Row 5", Day12Springs(getSpringRow2("?###????????"), getBrokenPatternPart2("3,2,1")).getCombinations(), 506250)
    checkSolution("Day 12 Part 1 Example", day12Part2Solution("inputs/day12/example.txt"), 525152)
    */

    checkSolution("Day 12 Part 1", day12Part2Solution("inputs/day12/part1.txt"))
}

enum class SpringCondition {
    Working, Broken, Unknown
}

fun findPatternMismatch(springs: List<SpringCondition>, brokenRanges: List<IntRange>): Int {
    //print("Ranges: ")
    //brokenRanges.forEach { print("$it, ")}
    //println()
    for(i in springs.indices) {
        //println(springs[i])
        if(
            springs[i] == SpringCondition.Broken && !brokenRanges.any { it.contains(i) } ||
            springs[i] == SpringCondition.Working && brokenRanges.any { it.contains(i) }) {
            return i
        }
    }
    return -1
}

class Day12Springs(private val springs: List<SpringCondition>, private val broken: List<Int>) {
    private val brokenRanges: MutableList<IntRange>
    private val rangeMoves = springs.size - broken.sumOf { it } - broken.size + 1
    private val rangeMovesLeft = MutableList(broken.size) { rangeMoves }

    init {
        var currentOffset = 0
        brokenRanges = broken.map { brokenSize ->
            val offset = currentOffset
            currentOffset += brokenSize + 1
            offset until offset + brokenSize
        }.toMutableList()


    }

    fun getCombinationsOrig(): Int {
        var validCombinations = 0
        while(rangeMovesLeft.any { it > 0 }) {
            val firstMismatch = findPatternMismatch(springs, brokenRanges)
            if(firstMismatch < 0) {
                validCombinations++
            }
            moveRangesOneSpot()

        }
        if(findPatternMismatch(springs, brokenRanges) < 0) {
            validCombinations++
        }
        return validCombinations
    }

    fun getCombinations(debug: Boolean = false): Int {
        var validCombinations = 0
        while(rangeMovesLeft.any { it > 0 }) {
            val firstMismatch = findPatternMismatch(springs, brokenRanges)

            if(debug) {
                brokenRanges.forEach { print("$it, ") }
                println(firstMismatch)

            }
            //println("First Mismatch $firstMismatch")
            if(firstMismatch < 0) {
                validCombinations++
                moveRangesOneSpot()

            }
            else {
                // We can skip ahead
                var rangeToMove = brokenRanges.lastIndex
                var spotsToMove = 1
                for(i in brokenRanges.indices) {
                    if(rangeMovesLeft[i] == 0 || firstMismatch < brokenRanges[i].first) {
                        rangeToMove = i - 1
                        break
                    }
                    if(firstMismatch <= brokenRanges[i].last) {
                        rangeToMove = i
                        spotsToMove = min(rangeMovesLeft[i], firstMismatch - brokenRanges[i].first + 1)
                        break
                    }
                }
                //println("Range to move $rangeToMove")
                if(rangeToMove < 0) {
                    break
                }
                moveRange(rangeToMove, spotsToMove)
            }

        }
        if(findPatternMismatch(springs, brokenRanges) < 0) {
            validCombinations++
        }
        return validCombinations
    }
    private fun moveRangesOneSpot() {
        val rangeToMove = rangeMovesLeft.indexOfLast { it > 0 }
        if(rangeToMove >= 0) {
            moveRange(rangeToMove)
        }
    }

    private fun moveRange(index: Int, spots: Int = 1) {
        rangeMovesLeft[index] = rangeMovesLeft[index] - spots
        brokenRanges[index] = brokenRanges[index].first + spots..brokenRanges[index].last + spots
        var nextRangeStart = brokenRanges[index].last + 2
        if (index != brokenRanges.lastIndex) {
            for (j in index + 1 until brokenRanges.size) {
                rangeMovesLeft[j] = rangeMovesLeft[index]
                brokenRanges[j] = nextRangeStart until nextRangeStart + broken[j]
                nextRangeStart += broken[j] + 1
            }
        }
    }
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
        val originalCount = Day12Springs(getSpringRow(parts[0]), getBrokenPattern(parts[1])).getCombinationsOrig()
        val newCount = Day12Springs(getSpringRow(parts[0]), getBrokenPattern(parts[1])).getCombinations()
        if(originalCount != newCount) {
            println("$springRow $originalCount $newCount")
            Day12Springs(getSpringRow(parts[0]), getBrokenPattern(parts[1])).getCombinations(true)
        }
        newCount
    }.toLong()

}


fun getBrokenPatternPart2(pattern: String): List<Int> {
    val base = pattern.split(",").map { it.toInt() }
    val brokenPattern = base.toMutableList()
    for(i in 0 until 4) {
        brokenPattern.addAll(base)
    }
    return brokenPattern
}


fun getSpringRow2(conditions: String): List<SpringCondition> {
    val base = conditions.map { when(it) {
        '?' -> SpringCondition.Unknown
        '#' -> SpringCondition.Broken
        else -> SpringCondition.Working
    } }
    val springPattern = base.toMutableList()
    for(i in 0 until 4) {
        springPattern.add(SpringCondition.Unknown)
        springPattern.addAll(base)
    }
    return springPattern
}

fun day12Part2Solution(path: String): Long {

    val matchesInLine = Channel<Long>(15)
    var count = 0.toLong()

    runBlocking {
        val lines = File(path).readLines()
        val size = lines.size
        lines.forEachIndexed { index, springRow ->
            CoroutineScope(Dispatchers.Default).launch {
                val parts = springRow.split(" ")
                val matches = Day12Springs(getSpringRow2(parts[0]), getBrokenPatternPart2(parts[1])).getCombinations().toLong()
                matchesInLine.send(matches)
                println("$index: $springRow $matches")
            }
        }

        for(i in 0 until size) {
            count += matchesInLine.receive()
        }
    }
    return count
}
