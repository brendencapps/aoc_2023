import java.io.File
import kotlin.math.max

fun main() {
    checkSolution("Day 16 Part 1 Example", day16Part1Solution("inputs/day16/example.txt"), 46)
    checkSolution("Day 16 Part 1", day16Part1Solution("inputs/day16/part1.txt"), 7185)
    checkSolution("Day 16 Part 2 Example", day16Part2Solution("inputs/day16/example.txt"), 51)
    checkSolution("Day 16 Part 2 Example", day16Part2Solution("inputs/day16/part1.txt"))
}

enum class FlowDirection {
    Left, Right, Up, Down
}
class Day16GridPoint(val input: Char, val row: Int, val col: Int) {
    var energized = false
    val visited = mutableMapOf(FlowDirection.Left to false, FlowDirection.Right to false, FlowDirection.Up to false, FlowDirection.Down to false)

}

class Day16(path: String) {
    val grid = File(path).readLines().mapIndexed { row, line ->
        line.mapIndexed { col, point ->
            Day16GridPoint(point, row, col)
        }
    }

    fun runFlow(startPoint: Day16GridPoint, flowDirection: FlowDirection) {
        var currentPoint: Day16GridPoint? = startPoint
        var currentDirection = flowDirection
        while(currentPoint != null && !currentPoint.visited[currentDirection]!!) {
            //println("Visiting point ${currentPoint.row} ${currentPoint.col} $currentDirection")
            currentPoint.energized = true
            currentPoint.visited[currentDirection] = true
            currentDirection = when(currentPoint.input) {
                '/' -> {
                    when(currentDirection) {
                        FlowDirection.Left -> FlowDirection.Down
                        FlowDirection.Right -> FlowDirection.Up
                        FlowDirection.Up -> FlowDirection.Right
                        FlowDirection.Down -> FlowDirection.Left
                    }
                }
                '\\' -> {
                    when(currentDirection) {
                        FlowDirection.Left -> FlowDirection.Up
                        FlowDirection.Right -> FlowDirection.Down
                        FlowDirection.Up -> FlowDirection.Left
                        FlowDirection.Down -> FlowDirection.Right
                    }
                }
                '|' -> {
                    if(currentDirection == FlowDirection.Right || currentDirection == FlowDirection.Left) {
                        val pointUp = getNextPoint(currentPoint, FlowDirection.Up)
                        if(pointUp != null) {
                            runFlow(pointUp, FlowDirection.Up)
                        }
                        FlowDirection.Down
                    }
                    else {
                        currentDirection
                    }
                }
                '-' -> {
                    if(currentDirection == FlowDirection.Up || currentDirection == FlowDirection.Down) {
                        val pointLeft = getNextPoint(currentPoint, FlowDirection.Left)
                        if(pointLeft != null) {
                            runFlow(pointLeft, FlowDirection.Left)
                        }
                        FlowDirection.Right
                    }
                    else {
                        currentDirection
                    }
                }
                else -> currentDirection
            }
            currentPoint = getNextPoint(currentPoint, currentDirection)
        }
    }

    private fun getNextPoint(point: Day16GridPoint, flowDirection: FlowDirection): Day16GridPoint? {
        when(flowDirection) {
            FlowDirection.Left -> {
                if(point.col == 0) {
                    return null
                }
                return grid[point.row][point.col - 1]
            }
            FlowDirection.Right -> {
                if(point.col == grid[0].lastIndex) {
                    return null
                }
                return grid[point.row][point.col + 1]
            }
            FlowDirection.Up -> {
                if(point.row == 0) {
                    return null
                }
                return grid[point.row - 1][point.col]
            }
            FlowDirection.Down -> {
                if(point.row == grid.lastIndex) {
                    return null
                }
                return grid[point.row + 1][point.col]
            }
        }
    }

    fun countEnergized(): Int {
        return grid.sumOf { row ->
            row.count { it.energized }
        }
    }

    fun resetGrid() {
        grid.forEach { row ->
            row.forEach {
                it.energized = false
                it.visited[FlowDirection.Left] = false
                it.visited[FlowDirection.Right] = false
                it.visited[FlowDirection.Up] = false
                it.visited[FlowDirection.Down] = false
            }
        }
    }
}

fun day16Part1Solution(path: String): Long {
    val problem = Day16(path)
    problem.runFlow(problem.grid[0][0], FlowDirection.Right)
    return problem.countEnergized().toLong()
}


fun day16Part2Solution(path: String): Long {

    val problem = Day16(path)
    var max = 0
    for(i in problem.grid.indices) {
        problem.runFlow(problem.grid[i][0], FlowDirection.Right)
        max = max(problem.countEnergized(), max)
        problem.resetGrid()
        problem.runFlow(problem.grid[i][problem.grid.lastIndex], FlowDirection.Left)
        max = max(problem.countEnergized(), max)
        problem.resetGrid()
    }
    for(i in problem.grid[0].indices) {
        problem.runFlow(problem.grid[0][i], FlowDirection.Down)
        max = max(problem.countEnergized(), max)
        problem.resetGrid()
        problem.runFlow(problem.grid[problem.grid.lastIndex][i], FlowDirection.Up)
        max = max(problem.countEnergized(), max)
        problem.resetGrid()

    }
    return max.toLong()
}