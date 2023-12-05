import java.io.File
import kotlin.math.pow

fun main() {

    checkSolution("Day 4 Part 1 Example", day4Part1Solution("inputs/day04/example.txt"), 13)
    checkSolution("Day 4 Part 1", day4Part1Solution("inputs/day04/part1.txt"), 20667)
    checkSolution("Day 4 Part 2 Example", day4Part2Solution("inputs/day04/example.txt"), 30)
    checkSolution("Day 4 Part 2", day4Part2Solution("inputs/day04/part1.txt"), 5833065)
}

class Card(card: String) {
    val matches: Int
    var copies: Int = 1

    init {
        val cardParts = card.substring(card.indexOf(':') + 1).trim().split(" | ")
        val winningNumbers = cardParts[0].trim().replace("\\s+".toRegex(), " ").split(" ").map { it.toInt() }
        val cardNumbers = cardParts[1].trim().replace("\\s+".toRegex(), " ").split(" ").map { it.toInt() }
        matches = cardNumbers.count { winningNumbers.contains(it) }
    }
}


fun day4Part1Solution(path: String): Int {
    return File(path).readLines().sumOf { line ->
        val card = Card(line)
        if(card.matches == 0) 0 else (2.0.pow((card.matches - 1).toDouble())).toInt()
    }
}

fun day4Part2Solution(path: String): Int {
    val cards = File(path).readLines().map { Card(it) }
    cards.forEachIndexed { index, card ->
        for(copy in 1 .. card.matches) {
            cards[index + copy].copies += card.copies
        }
    }

    return cards.sumOf { it.copies }

}
