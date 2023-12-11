import java.io.File

fun main() {
    checkSolution("Day 10 Part 1 Example", day10Part1Solution("inputs/day10/example.txt"), 4)
    checkSolution("Day 10 Part 1 Example 2", day10Part1Solution("inputs/day10/example2.txt"), 8)
    checkSolution("Day 10 Part 1", day10Part1Solution("inputs/day10/part1.txt"), 6717)
    checkSolution("Day 10 Part 2 Example 1", day10Part2Solution("inputs/day10/example3.txt"), 4)
    checkSolution("Day 10 Part 2 Example 2", day10Part2Solution("inputs/day10/example4.txt"), 8)
    checkSolution("Day 10 Part 2", day10Part2Solution("inputs/day10/part1.txt"))
}

enum class PipeDirection {
    North, South, East, West;

    fun reverseDirection(): PipeDirection {
        return when(this) {
            North -> South
            South -> North
            East -> West
            West -> East
        }
    }
}

class Pipe(letter: Char, val row: Int, val col: Int)
{
    val hasConnection = mutableListOf<PipeDirection>()
    val inside = mutableListOf<PipeDirection>()
    val isStart = letter == 'S'
    val connectedPipes = mutableMapOf<PipeDirection, Pipe>()
    var isInLoop = false
    var isInside = false

    init {
        when(letter) {
            '|' -> {
                hasConnection.add(PipeDirection.North)
                hasConnection.add(PipeDirection.South)
            }
            '-' -> {
                hasConnection.add(PipeDirection.East)
                hasConnection.add(PipeDirection.West)
            }
            'L' -> {
                hasConnection.add(PipeDirection.North)
                hasConnection.add(PipeDirection.East)
            }
            'J' -> {
                hasConnection.add(PipeDirection.North)
                hasConnection.add(PipeDirection.West)
            }
            '7' -> {
                hasConnection.add(PipeDirection.South)
                hasConnection.add(PipeDirection.West)
            }
            'F' -> {
                hasConnection.add(PipeDirection.South)
                hasConnection.add(PipeDirection.East)
            }
        }
    }

    fun updateStartPipe(pipes: List<List<Pipe>>) {
        if(row > 0) {
            val pipeToNorth = pipes[row - 1][col]
            if(pipeToNorth.hasConnection.contains(PipeDirection.South)) {
                hasConnection.add(PipeDirection.North)
            }
        }
        if(row < pipes.lastIndex) {
            val pipeToSouth = pipes[row + 1][col]
            if(pipeToSouth.hasConnection.contains(PipeDirection.North)) {
                hasConnection.add(PipeDirection.South)
            }
        }

        if(col > 0) {
            val pipeToWest = pipes[row][col - 1]
            if(pipeToWest.hasConnection.contains(PipeDirection.East)) {
                hasConnection.add(PipeDirection.West)
            }
        }
        if(col < pipes[row].lastIndex) {
            val pipeToEast = pipes[row][col + 1]
            if(pipeToEast.hasConnection.contains(PipeDirection.West)) {
                hasConnection.add(PipeDirection.East)
            }
        }

    }

    fun getConnectedPipe(pipes: List<List<Pipe>>, direction: PipeDirection): Pipe {
        return when(direction) {
            PipeDirection.North -> pipes[row - 1][col]
            PipeDirection.South -> pipes[row + 1][col]
            PipeDirection.East -> pipes[row][col + 1]
            PipeDirection.West -> pipes[row][col - 1]
        }
    }

    fun connectPipe(other: Pipe, direction: PipeDirection) {
        connectedPipes[direction] = other
        other.connectedPipes[direction.reverseDirection()] = this
    }
}

class Pipes(path: String) {

    var maxSteps: Long = 0
    val pipes: List<List<Pipe>>

    init {
        pipes = File(path).readLines().mapIndexed {row, line ->
            line.mapIndexed { col, pipe -> Pipe(pipe, row, col) }
        }

        val startPipe = pipes.first { row -> row.any { it.isStart } }.first { it.isStart }
        startPipe.updateStartPipe(pipes)
        startPipe.isInLoop = true

        check(startPipe.hasConnection.size == 2)

        var direction1 = startPipe.hasConnection.first()
        var direction2 = startPipe.hasConnection.last()

        var pipePath1 = startPipe
        var pipePath2 = startPipe
        pipePath1.isInLoop = true
        pipePath2.isInLoop = true


        var steps = 0.toLong()
        while(true) {
            val nextPipePath1 = pipePath1.getConnectedPipe(pipes, direction1)
            val nextPipePath2 = pipePath2.getConnectedPipe(pipes, direction2)
            nextPipePath1.isInLoop = true
            nextPipePath2.isInLoop = true
            pipePath1.connectPipe(nextPipePath1, direction1)
            pipePath2.connectPipe(nextPipePath2, direction2)
            direction1 = nextPipePath1.hasConnection.first { it != direction1.reverseDirection() }
            direction2 = nextPipePath2.hasConnection.first { it != direction2.reverseDirection() }

            if(pipePath1 == nextPipePath2 || pipePath2 == nextPipePath1) {
                maxSteps = steps
                break
            }
            if(nextPipePath2 == nextPipePath1) {
                maxSteps = steps + 1
                break
            }
            pipePath1 = nextPipePath1
            pipePath2 = nextPipePath2
            steps++
        }
    }

    /**
     * Searching from the top left to find a pipe in the loop must result in a corner pipe that goes south and
     * east.
     */
    fun findTopLeftCorner(): Pipe {
        for(i in pipes.indices) {
            for(j in pipes[i].indices) {
                if(pipes[i][j].isInLoop) {
                    return pipes[i][j]
                }
            }
        }
        return pipes[0][0]
    }
}

fun day10Part1Solution(path: String): Long {

    return Pipes(path).maxSteps
}


fun day10Part2Solution(path: String): Long {

    val pipes = Pipes(path)

    val firstPipe = pipes.findTopLeftCorner()
    check(firstPipe.hasConnection.contains(PipeDirection.South))
    check(firstPipe.hasConnection.contains(PipeDirection.East))
    firstPipe.inside.add(PipeDirection.South)
    firstPipe.inside.add(PipeDirection.East)
    var currentInsideVector = PipeDirection.East
    var currentPipe = firstPipe.connectedPipes[PipeDirection.South]!!
    var currentDirection = PipeDirection.South
    while(currentPipe != firstPipe) {
        currentPipe.inside.add(currentInsideVector)
        if(
            (currentPipe.hasConnection.contains(PipeDirection.North) && currentPipe.hasConnection.contains(PipeDirection.South)) ||
            (currentPipe.hasConnection.contains(PipeDirection.East) && currentPipe.hasConnection.contains(PipeDirection.West))
        ) {
            // Straight pipe.  Just move.
            currentPipe = currentPipe.connectedPipes[currentDirection]!!
        }
        else {
            val nextDirection = currentPipe.hasConnection.first { it != currentDirection.reverseDirection() }
            val nextInsideDirection = when(currentDirection) {
                PipeDirection.North -> if(nextDirection == PipeDirection.East) {
                    if(currentInsideVector == PipeDirection.West) {
                        PipeDirection.North
                    }
                    else {
                        PipeDirection.South
                    }
                } else {
                    if(currentInsideVector == PipeDirection.West) {
                        PipeDirection.South
                    }
                    else {
                        PipeDirection.North
                    }
                }
                PipeDirection.South -> if(nextDirection == PipeDirection.East) {
                    if(currentInsideVector == PipeDirection.West) {
                        PipeDirection.South
                    }
                    else {
                        PipeDirection.North
                    }
                } else {
                    if(currentInsideVector == PipeDirection.West) {
                        PipeDirection.North
                    }
                    else {
                        PipeDirection.South
                    }
                }
                PipeDirection.East -> if(nextDirection == PipeDirection.North) {
                    if(currentInsideVector == PipeDirection.North) {
                        PipeDirection.West
                    }
                    else {
                        PipeDirection.East
                    }
                } else {
                    if(currentInsideVector == PipeDirection.North) {
                        PipeDirection.East
                    }
                    else {
                        PipeDirection.West
                    }
                }
                PipeDirection.West -> if(nextDirection == PipeDirection.North) {
                    if(currentInsideVector == PipeDirection.North) {
                        PipeDirection.East
                    }
                    else {
                        PipeDirection.West
                    }
                } else {
                    if(currentInsideVector == PipeDirection.North) {
                        PipeDirection.West
                    }
                    else {
                        PipeDirection.East
                    }
                }
            }
            currentPipe.inside.add(nextInsideDirection)
            currentInsideVector = nextInsideDirection
            currentDirection = nextDirection
            currentPipe = currentPipe.connectedPipes[nextDirection]!!
        }
    }

    val total = pipes.pipes.sumOf {pipeRow ->
        pipeRow.count {pipe ->
            if(pipe.row > 0 && pipe.col > 0 && !pipe.isInLoop) {
                for(i in 0 until pipe.col) {
                    val pipeToLeft = pipes.pipes[pipe.row][pipe.col - i - 1]
                    if(pipeToLeft.isInLoop) {
                        pipe.isInside = pipeToLeft.inside.contains(PipeDirection.East)
                        break
                    }
                }
                pipe.isInside
            }
            else {
                false
            }
        }.toLong()
    }

    return total
}
