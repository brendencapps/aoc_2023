import java.io.File

fun main() {

    checkSolution("Day 7 Part 1 Example", day7Part1Solution("inputs/day07/example.txt"), 6440)
    checkSolution("Day 7 Part 1", day7Part1Solution("inputs/day07/part1.txt"), 254024898)
    checkSolution("Day 7 Part 2 Example", day7Part2Solution("inputs/day07/example.txt"), 5905)
    checkSolution("Day 7 Part 2", day7Part2Solution("inputs/day07/part1.txt"), 254115617)
}

enum class HandType {
    FiveOfAKind,
    FourOfAKind,
    FullHouse,
    ThreeOfAKind,
    TwoPair,
    OnePair,
    HighCard

}
class CamelCardHand(val cards: List<Int>, val bid: Int) {
    val handType: HandType

    init {
        val cardGrouping = cards.groupingBy { it }.eachCount()
        handType = if(cardGrouping.any { it.value == 5}) {
            HandType.FiveOfAKind
        } else if(cardGrouping.any { it.value == 4}) {
            HandType.FourOfAKind
        } else if(cardGrouping.any { it.value == 3}) {
            if(cardGrouping.any { it.value == 2}) {
                HandType.FullHouse
            }
            else {
                HandType.ThreeOfAKind
            }
        }else if(cardGrouping.any { it.value == 2}) {
            if(cardGrouping.count { it.value == 2 } == 2) {
                HandType.TwoPair
            }
            else {
                HandType.OnePair
            }
        } else {
            HandType.HighCard
        }

    }

    fun compareHands(other: CamelCardHand): Int {
        if(handType.ordinal == other.handType.ordinal ) {
            for(i in cards.indices) {
                if(cards[i] > other.cards[i]) {
                    return -1
                }
                else if(cards[i] < other.cards[i]){
                    return 1
                }
            }
            return 0
        }
        else if(handType.ordinal < other.handType.ordinal ) {
            return -1
        }
        else{
            return 1
        }
    }
}

class CamelCardHand2(val cards: List<Int>, val bid: Int) {
    val handType: HandType

    init {
        val cardGrouping = cards.filter { it != 1 } .groupingBy { it }.eachCount()
        val jokers = cards.count { it == 1 }
        handType = if(cardGrouping.any { it.value == 5} || jokers == 4 || jokers == 5) {
            HandType.FiveOfAKind
        }
        else if(cardGrouping.any { it.value == 4}) {
            if(jokers == 1) {
                HandType.FiveOfAKind
            }
            else {
                HandType.FourOfAKind
            }
        }
        else if(cardGrouping.any { it.value == 3}) {
            if(jokers == 2) {
                HandType.FiveOfAKind
            }
            else if(jokers == 1) {
                HandType.FourOfAKind
            }
            else if(cardGrouping.any { it.value == 2}) {
                HandType.FullHouse
            }
            else {
                HandType.ThreeOfAKind
            }
        }
        else if(cardGrouping.any { it.value == 2}) {
            if(jokers == 3) {
                HandType.FiveOfAKind
            }
            else if(jokers == 2) {
                HandType.FourOfAKind
            }
            else if(jokers == 1) {
                if(cardGrouping.count { it.value == 2 } == 2) {
                    HandType.FullHouse
                }
                else {
                    HandType.ThreeOfAKind
                }
            }
            else if(cardGrouping.count { it.value == 2 } == 2) {
                HandType.TwoPair
            }
            else {
                HandType.OnePair
            }
        } else {
            if(jokers == 3) {
                HandType.FourOfAKind
            }
            else if(jokers == 2) {
                HandType.ThreeOfAKind
            }
            else if(jokers == 1) {
                HandType.OnePair
            }
            else {
                HandType.HighCard
            }
        }

    }

    fun compareHands(other: CamelCardHand2): Int {
        if(handType.ordinal == other.handType.ordinal ) {
            for(i in cards.indices) {
                if(cards[i] > other.cards[i]) {
                    return -1
                }
                else if(cards[i] < other.cards[i]){
                    return 1
                }
            }
            return 0
        }
        else if(handType.ordinal < other.handType.ordinal ) {
            return -1
        }
        else{
            return 1
        }
    }
}

fun day7Part1Solution(path: String): Long {


    val hands = File(path).readLines().map { line ->
        val parts = line.split(" ")
        CamelCardHand(
            parts[0].map {
                when(it) {
                    '2' -> 2
                    '3' -> 3
                    '4' -> 4
                    '5' -> 5
                    '6' -> 6
                    '7' -> 7
                    '8' -> 8
                    '9' -> 9
                    'T' -> 10
                    'J' -> 11
                    'Q' -> 12
                    'K' -> 13
                    'A' -> 14
                    else -> 0
                }
            }, parts[1].toInt()
        )
    }.sortedWith { a, b ->
        a.compareHands(b)
    }

    return hands.mapIndexed { index, hand ->
        val rank = hands.size - index
        println("${hand.cards} ${hand.handType} ${hand.bid} $rank ")
        (rank * hand.bid).toLong()
    }.sum()
}


fun day7Part2Solution(path: String): Long {


    val hands = File(path).readLines().map { line ->
        val parts = line.split(" ")
        CamelCardHand2(
            parts[0].map {
                when(it) {
                    '2' -> 2
                    '3' -> 3
                    '4' -> 4
                    '5' -> 5
                    '6' -> 6
                    '7' -> 7
                    '8' -> 8
                    '9' -> 9
                    'T' -> 10
                    'J' -> 1
                    'Q' -> 11
                    'K' -> 12
                    'A' -> 13
                    else -> 0
                }
            }, parts[1].toInt()
        )
    }.sortedWith { a, b ->
        a.compareHands(b)
    }

    return hands.mapIndexed { index, hand ->
        val rank = hands.size - index
        println("${hand.cards} ${hand.handType} ${hand.bid} $rank ")
        (rank * hand.bid).toLong()
    }.sum()
}
