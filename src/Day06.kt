data class Race(val time: Long, val recordDistance: Long) {
    fun getWinningPossibilities(): Long {
        val raceRange = 1 until time
        return (raceRange.indexOfLast { breaksRecord(it) } - raceRange.indexOfFirst { breaksRecord(it) } + 1).toLong()
    }

    private fun breaksRecord(holdTime: Long): Boolean {
        return (time - holdTime) * holdTime > recordDistance
    }
}

fun main() {

    val exampleRace = listOf(Race(7, 9), Race(15, 40), Race(30, 200))
    val race = listOf(Race(44, 208), Race(80, 1581), Race(65, 1050), Race(72, 1102))

    checkSolution("Day 6 Part 1 Example", day6Part1Solution(exampleRace), 288)
    checkSolution("Day 6 Part 1", day6Part1Solution(race), 32076)
    checkSolution("Day 6 Part 2 Example", Race(71530, 940200).getWinningPossibilities(), 71503)
    checkSolution("Day 6 Part 2", Race(44806572, 208158110501102).getWinningPossibilities(), 34278221)
}

fun day6Part1Solution(races: List<Race>): Long {

    var result = 1.toLong()
    races.map { it.getWinningPossibilities() }.forEach{ result *= it }
    return result

}

