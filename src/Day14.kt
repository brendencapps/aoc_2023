import java.io.File

fun main() {
    checkSolution("Day 14 Part 1 Example", day14Part1Solution("inputs/day14/example.txt"), 136)
    checkSolution("Day 14 Part 1", day14Part1Solution("inputs/day14/part1.txt"), 110677)
    checkSolution("Day 14 Part 2 Example", day14Part2Solution("inputs/day14/example.txt"), 64)
    checkSolution("Day 14 Part 2 Example", day14Part2Solution("inputs/day14/part1.txt"), 90551)
}

fun day14Part1Solution(path: String): Int {
    val dish = ReflectorDish(File(path).readLines())
    dish.moveRocksNorth()
    return dish.computeNorthLoad()
}

enum class ReflectorDishPoint {
    RoundRock,
    CubeRock,
    Empty;

    override fun toString(): String {
        return when(this) {
            RoundRock ->  "O"
            CubeRock -> "#"
            Empty -> "."
        }

    }

}

class ReflectorDish(input: List<String>) {

    private val dishGrid = input.map { dishRow ->
        dishRow.map { dishPoint ->
            when (dishPoint) {
                'O' -> {
                    ReflectorDishPoint.RoundRock
                }
                '#' -> {
                    ReflectorDishPoint.CubeRock
                }
                else -> {
                    ReflectorDishPoint.Empty
                }
            }
        }.toMutableList()
    }

    fun computeNorthLoad(): Int {
        return dishGrid.mapIndexed { rowIndex, row ->
            row.sumOf { point ->
                if (point == ReflectorDishPoint.RoundRock) {
                    dishGrid.size - rowIndex
                } else {
                    0
                }
            }
        }.sum()
    }

    fun moveRocksNorth() {
        for(col in dishGrid[0].indices) {
            var edge = 0
            for(row in dishGrid.indices) {
                when(dishGrid[row][col]) {
                    ReflectorDishPoint.RoundRock -> {
                        if (row != edge) {
                            dishGrid[edge][col] = ReflectorDishPoint.RoundRock
                            dishGrid[row][col] = ReflectorDishPoint.Empty
                        }
                        edge++
                    }
                    ReflectorDishPoint.CubeRock -> {
                        edge = row + 1
                    }
                    ReflectorDishPoint.Empty -> {}
                }

            }
        }
    }

    private fun moveRocksSouth() {
        for(col in dishGrid[0].indices) {
            var edge = dishGrid.lastIndex
            for(row in dishGrid.lastIndex downTo 0) {
                when(dishGrid[row][col]) {
                    ReflectorDishPoint.RoundRock -> {
                        if (row != edge) {
                            dishGrid[edge][col] = ReflectorDishPoint.RoundRock
                            dishGrid[row][col] = ReflectorDishPoint.Empty
                        }
                        edge--
                    }
                    ReflectorDishPoint.CubeRock -> {
                        edge = row - 1
                    }
                    ReflectorDishPoint.Empty -> {}
                }

            }
        }
    }

    private fun moveRocksWest() {
        for(row in dishGrid.indices) {
            var edge = 0
            for(col in dishGrid[row].indices) {
                when(dishGrid[row][col]) {
                    ReflectorDishPoint.RoundRock -> {
                        if (col != edge) {
                            dishGrid[row][edge] = ReflectorDishPoint.RoundRock
                            dishGrid[row][col] = ReflectorDishPoint.Empty
                        }
                        edge++
                    }
                    ReflectorDishPoint.CubeRock -> {
                        edge = col + 1
                    }
                    ReflectorDishPoint.Empty -> {}
                }
            }
        }
    }

    private fun moveRocksEast() {
        for(row in dishGrid.indices) {
            var edge = dishGrid[row].lastIndex
            for(col in dishGrid[row].lastIndex downTo 0) {
                when(dishGrid[row][col]) {
                    ReflectorDishPoint.RoundRock -> {
                        if (col != edge) {
                            dishGrid[row][edge] = ReflectorDishPoint.RoundRock
                            dishGrid[row][col] = ReflectorDishPoint.Empty
                        }
                        edge--
                    }
                    ReflectorDishPoint.CubeRock -> {
                        edge = col - 1
                    }
                    ReflectorDishPoint.Empty -> {}
                }
            }
        }
    }

    fun moveCycle(): String {
        var cycleResult = ""
        moveRocksNorth()
        cycleResult += "${computeNorthLoad()} "
        moveRocksWest()
        cycleResult += "${computeNorthLoad()} "
        moveRocksSouth()
        cycleResult += "${computeNorthLoad()} "
        moveRocksEast()
        cycleResult += "${computeNorthLoad()}"
        return cycleResult
    }
}


fun day14Part2Solution(path: String): Long {
    val dish = ReflectorDish(File(path).readLines())

    val cycleResult = mutableListOf<String>()
    while(true) {
        val result = dish.moveCycle()
        if(cycleResult.contains(result)) {
            // We have found a repeat.
            val startOfCycle = cycleResult.indexOf(result)
            val cycleSize = cycleResult.size - cycleResult.indexOf(result)
            val indexOfTarget = (1000000000 - startOfCycle) % cycleSize + startOfCycle - 1
            return cycleResult[indexOfTarget].split(" ").last().toLong()

        }
        cycleResult.add(result)
    }
}