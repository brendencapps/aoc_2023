import java.io.File

fun main() {
    checkSolution("Day 2 Part 1 Example", day2Part1Solution("inputs/day02/example.txt"), 8)
    checkSolution("Day 2 Part 1", day2Part1Solution("inputs/day02/part1.txt"), 2406)
    checkSolution("Day 2 Part 2 Example", day2Part2Solution("inputs/day02/example.txt"), 2286)
    checkSolution("Day 2 Part 2", day2Part2Solution("inputs/day02/part1.txt"), 78375)
}

class Day02Game(game: String) {
    val gameId: Int
    val rounds: List<Day02GameRound>

    init {
        val regex = "Game (\\d*): (.*)".toRegex()
        val (gameId, results) = regex.find(game)?.destructured ?: error("Error parsing game $game")
        this.gameId = gameId.toInt()
        rounds = results.split("; ").map { Day02GameRound(it) }
    }
}

class Day02GameRound(round: String) {
    var red: Int = 0
    var green: Int = 0
    var blue: Int = 0
    init {
        round.split(", ").forEach { cubes ->
            val cubeParts = cubes.split(" ")
            when(cubeParts[1]) {
                "red" -> red = cubeParts[0].toInt()
                "green" -> green = cubeParts[0].toInt()
                "blue" -> blue = cubeParts[0].toInt()
            }
        }
    }
}

fun day2Part1Solution(path: String): Int {
    return File(path).readLines().sumOf { value ->
        val game = Day02Game(value)
        if(game.rounds.any{ it.red > 12 || it.green > 13 || it.blue > 14 }) {
            0
        }
        else {
            game.gameId
        }
    }
}

fun day2Part2Solution(path: String): Int {
    return File(path).readLines().sumOf { value ->
        val game = Day02Game(value)
        game.rounds.maxOf { it.red } * game.rounds.maxOf { it.green } * game.rounds.maxOf { it.blue }
    }
}
