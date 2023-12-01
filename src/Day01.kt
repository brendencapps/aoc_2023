import java.io.File

fun main() {

    checkSolution("Day 1 Part 1", day1Part1Solution("inputs/day01/part1.txt"), 54697)
    checkSolution("Day 1 Part 2", day1Part2Solution("inputs/day01/part1.txt"), 54885)
    checkSolution("Day 1 Part 2 Example", day1Part2Solution("inputs/day01/example.txt"), 281)
}

fun day1Part1Solution(path: String): Int {
    return File(path).readLines().sumOf { value ->
        val first = value.first { it.isDigit() }.digitToInt()
        val last = value.last { it.isDigit() }.digitToInt()
        first * 10 + last
    }
}

fun String.firstMatch(list: Set<String>): String? {
    for(index in indices) {
        for(match in list) {
            if(substring(index).startsWith(match))
                return match
        }
    }
    return null
}
fun String.lastMatch(list: Set<String>): String? {
    for(index in indices) {
        for(match in list) {
            if(substring(length - index - 1).startsWith(match))
                return match
        }
    }
    return null
}

fun day1Part2Solution(path: String): Int {
    return File(path).readLines().sumOf { value ->
        val numberMap = mapOf(
            "0" to 0,
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
            "6" to 6,
            "7" to 7,
            "8" to 8,
            "9" to 9,
            "zero" to 0,
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9
        )
        val firstDigit = value.firstMatch(numberMap.keys)
        val lastDigit = value.lastMatch(numberMap.keys)
        if (firstDigit == null || lastDigit == null) {
            0
        } else {
            (numberMap[firstDigit] ?: 0) * 10 + (numberMap[lastDigit] ?: 0)
        }

    }
}


