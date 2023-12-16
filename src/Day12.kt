import java.io.File

fun main() {


    checkSolution("Example Row 1", Day12Springs(getSpringRow("???.###"), getBrokenPattern("1,1,3")).getCombinationsRecursive(), 1)
    checkSolution("Example Row 2", Day12Springs(getSpringRow(".??..??...?##."), getBrokenPattern("1,1,3")).getCombinationsRecursive(), 4)
    checkSolution("Example Row 3", Day12Springs(getSpringRow("?#?#?#?#?#?#?#?"), getBrokenPattern("1,3,1,6")).getCombinationsRecursive(), 1)
    checkSolution("Example Row 4", Day12Springs(getSpringRow("????.#...#..."), getBrokenPattern("4,1,1")).getCombinationsRecursive(), 1)
    checkSolution("Example Row 5", Day12Springs(getSpringRow("????.######..#####."), getBrokenPattern("1,6,5")).getCombinationsRecursive(), 4)
    checkSolution("Example Row 5", Day12Springs(getSpringRow("?###????????"), getBrokenPattern("3,2,1")).getCombinationsRecursive(), 10)
    checkSolution("Day 12 Part 1 Example", day12Part1Solution("inputs/day12/example.txt"), 21)
    checkSolution("Day 12 Part 1", day12Part1Solution("inputs/day12/part1.txt"), 7402)

    checkSolution("Part 2 Example Row 1", Day12Springs(getSpringRow2("???.###"), getBrokenPatternPart2("1,1,3")).getCombinationsRecursive(), 1)
    checkSolution("Part 2 Example Row 2", Day12Springs(getSpringRow2(".??..??...?##."), getBrokenPatternPart2("1,1,3")).getCombinationsRecursive(), 16384)
    checkSolution("Part 2 Example Row 3", Day12Springs(getSpringRow2("?#?#?#?#?#?#?#?"), getBrokenPatternPart2("1,3,1,6")).getCombinationsRecursive(), 1)
    checkSolution("Part 2 Example Row 4", Day12Springs(getSpringRow2("????.#...#..."), getBrokenPatternPart2("4,1,1")).getCombinationsRecursive(), 16)
    checkSolution("Part 2 Example Row 5", Day12Springs(getSpringRow2("????.######..#####."), getBrokenPatternPart2("1,6,5")).getCombinationsRecursive(), 2500)
    checkSolution("Part 2 Example Row 5", Day12Springs(getSpringRow2("?###????????"), getBrokenPatternPart2("3,2,1")).getCombinationsRecursive(), 506250)
    checkSolution("Day 12 Part 2 Example", day12Part2Solution("inputs/day12/example.txt"), 525152)

    checkSolution("Day 12 Part 2", day12Part2Solution("inputs/day12/part1.txt"), 3384337640277)

}

enum class SpringCondition {
    Working, Broken, Unknown
}


class Day12Springs(private val springs: List<SpringCondition>, private val broken: List<Int>) {

    private val combinationCache = mutableMapOf<String, Long>()
    fun getCombinationsRecursive(patternPos: Int = 0, blockPos: Int = 0, currentBlockSize: Int = 0): Long {
        val key = "${patternPos}_${blockPos}_${currentBlockSize}"
        return combinationCache[key] ?: getCombinations(key, patternPos, blockPos, currentBlockSize)
    }

    private fun getCombinations(key: String, patternPos: Int, blockPos: Int, currentBlockSize: Int): Long {
        if(patternPos == springs.size) {
            return if((blockPos == broken.size && currentBlockSize == 0) || (blockPos == broken.lastIndex && currentBlockSize == broken.last())) {
                1.toLong()
            }
            else {
                0.toLong()
            }
        }
        var combinations = 0.toLong()

        if(springs[patternPos] == SpringCondition.Working || springs[patternPos] == SpringCondition.Unknown) {
            if (currentBlockSize == 0) {
                combinations += getCombinationsRecursive(patternPos + 1, blockPos, 0)
            }
            else if(blockPos <= broken.lastIndex && broken[blockPos] == currentBlockSize) {
                combinations += getCombinationsRecursive(patternPos + 1, blockPos + 1, 0)
            }
        }

        if(springs[patternPos] == SpringCondition.Broken || springs[patternPos] == SpringCondition.Unknown) {
            combinations += getCombinationsRecursive(patternPos + 1, blockPos, currentBlockSize + 1)
        }

        combinationCache[key] = combinations
        return combinations
    }


}


fun getSpringRow(conditions: String): List<SpringCondition> {
    val conditionsCompressed = conditions.replace("(\\.)+".toRegex(), ".")
    println("$conditions $conditionsCompressed")
    return conditionsCompressed.map { when(it) {
        '?' -> SpringCondition.Unknown
        '#' -> SpringCondition.Broken
        else -> SpringCondition.Working
    } }
}

fun getBrokenPattern(pattern: String, copies: Int = 0): List<Int> {
    return pattern.split(",").map { it.toInt() }
}

fun day12Part1Solution(path: String): Long {

    return File(path).readLines().sumOf { springRow ->
        val parts = springRow.split(" ")
        Day12Springs(getSpringRow(parts[0]), getBrokenPattern(parts[1])).getCombinationsRecursive(0, 0, 0)
    }

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
    val conditionsCompressed = conditions.replace("(\\.)+".toRegex(), ".")
    val base = conditionsCompressed.map { when(it) {
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
    val lines = File(path).readLines()
    var total = 0.toLong()
    lines.forEachIndexed { index, springRow ->
        val parts = springRow.split(" ")
        val matches = Day12Springs(getSpringRow2(parts[0]), getBrokenPatternPart2(parts[1])).getCombinationsRecursive(0, 0, 0)
        total += matches
        println("$index: $springRow $matches")
    }

    return total
}


