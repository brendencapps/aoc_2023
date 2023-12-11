import java.io.File

fun main() {
    checkSolution("Day 10 Part 1 Example", day10Part1Solution("inputs/day10/example.txt"), 4)
    checkSolution("Day 10 Part 1 Example 2", day10Part1Solution("inputs/day10/example2.txt"), 8)
    checkSolution("Day 10 Part 1", day10Part1Solution("inputs/day10/part1.txt"), 6717)
    //checkSolution("Day 10 Part 2 Example", day10Part2Solution("inputs/day10/example.txt"), 2)
    //checkSolution("Day 10 Part 2", day10Part2Solution("inputs/day10/part1.txt"))
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

/*
class Pipe(letter: Char, val row: Int, val col: Int) {
    val connections = when(letter) {
        '|' -> listOf(PipeDirection.North, PipeDirection.South)
        '-' -> listOf(PipeDirection.East, PipeDirection.West)
        'L' -> listOf(PipeDirection.North, PipeDirection.East)
        'J' -> listOf(PipeDirection.North, PipeDirection.West)
        '7' -> listOf(PipeDirection.South, PipeDirection.West)
        'F' -> listOf(PipeDirection.South, PipeDirection.East)
        else -> listOf()
    }
    val isStart = letter == 'S'
}
*/

class Pipe(val letter: Char, val row: Int, val col: Int)
{
    val hasConnection = mutableListOf<PipeDirection>()
    val inside = mutableListOf<PipeDirection>()
    val isStart = letter == 'S'
    val connectedPipes = mutableMapOf<PipeDirection, Pipe>()
    var isInLoop = false

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

        fun computeInsideDirection(from: Pipe, fromDirection: PipeDirection) {
            if(hasConnection.contains(PipeDirection.North) && hasConnection.contains(PipeDirection.South)) {
                inside.addAll(from.inside)
            }
            if(hasConnection.contains(PipeDirection.East) && hasConnection.contains(PipeDirection.West)) {
                inside.addAll(from.inside)
            }
            if(hasConnection.contains(PipeDirection.North) && hasConnection.contains(PipeDirection.East)) {
                if()
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

class PipeEntry(val pipe: Pipe, val directionFrom: PipeDirection, val insideDirection: List<PipeDirection> = listOf())

class Pipes(path: String) {

    var maxSteps: Long = 0
    val pipes: List<List<Pipe>>

    val startPipe: Pipe

    init {
        pipes = File(path).readLines().mapIndexed {row, line ->
            line.mapIndexed { col, pipe -> Pipe(pipe, row, col) }
        }

        startPipe = pipes.first { row -> row.any { it.isStart } }.first { it.isStart }
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

    fun findFirstInLoop(): Pipe {
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

fun getPipesConnectedToStart(startPipe: Pipe, pipes: List<List<Pipe>>): List<PipeEntry> {
    val connectedPipes = mutableListOf<PipeEntry>()
    if(startPipe.row > 0) {
        val pipeToNorth = pipes[startPipe.row - 1][startPipe.col]
        if(pipeToNorth.hasConnection.contains(PipeDirection.South)) {
            PipeEntry(pipeToNorth, PipeDirection.South)
        }
    }
    if(startPipe.row < pipes.lastIndex) {
        val pipeToSouth = pipes[startPipe.row + 1][startPipe.col]
        if(pipeToSouth.hasConnection.contains(PipeDirection.North)) {
            connectedPipes.add(PipeEntry(pipeToSouth, PipeDirection.North))
        }
    }

    if(startPipe.col > 0) {
        val pipeToWest = pipes[startPipe.row][startPipe.col - 1]
        if(pipeToWest.hasConnection.contains(PipeDirection.East)) {
            connectedPipes.add(PipeEntry(pipeToWest, PipeDirection.East))
        }
    }
    if(startPipe.col < pipes[startPipe.row].lastIndex) {
        val pipeToEast = pipes[startPipe.row][startPipe.col + 1]
        if(pipeToEast.hasConnection.contains(PipeDirection.West)) {
            connectedPipes.add(PipeEntry(pipeToEast, PipeDirection.West))
        }
    }
    return connectedPipes
}

fun day10Part2Solution(path: String): Long {

    val pipes = Pipes(path)

    val firstInLoop = pipes.findFirstInLoop()
    // We know that we started searching from outside so wherever this points it is pointing to inside.
    firstInLoop.inside.addAll(firstInLoop.hasConnection)
    var currentInLoop = firstInLoop.connectedPipes[firstInLoop.hasConnection[0]]
    var currentDirection = firstInLoop.hasConnection[0]
    while(currentInLoop != firstInLoop) {

    }

    return 0.toLong()
}
/*

fun pipeIsInside(pipe: Pipe, pipes: List<List<Pipe>>, pipesInCycle: List<Pipe>): Boolean {
    for(i in 0 until pipe.row) {
        val pipeToCheck = pipes[i][pipe.col]
        if(pipesInCycle.contains(pipeToCheck) &&
    }
}


fun pipeIsInsideNorth(pipe: Pipe, pipes: List<List<Pipe>>, pipesInCycle: List<Pipe>): Boolean {
    for(i in 0 until pipe.row) {
        val pipeToCheck = pipes[i][pipe.col]
        if(pipesInCycle.contains(pipeToCheck) && pipeToCheck.hasConnection.contains(PipeDirection.East) && pipeToCheck.hasConnection.contains(PipeDirection.West)) {
            return true
        }
    }
    return false
}


fun pipeIsInsideSouth(pipe: Pipe, pipes: List<List<Pipe>>, pipesInCycle: List<Pipe>): Boolean {
    for(i in pipe.row + 1 until pipes.size) {
        val pipeToCheck = pipes[i][pipe.col]
        if(pipesInCycle.contains(pipeToCheck) && pipeToCheck.hasConnection.contains(PipeDirection.East) && pipeToCheck.hasConnection.contains(PipeDirection.West)) {
            return true
        }
    }
    return false
}

*/