import java.io.File
import java.util.*

fun main() {
    checkSolution("Day 17 Part 1 Example", day17Part1Solution("inputs/day17/example.txt"), 102)
    checkSolution("Day 17 Part 1", day17Part1Solution("inputs/day17/part1.txt"), 959) // 963
    checkSolution("Day 17 Part 2 Example", day17Part2Solution("inputs/day17/example.txt"), 94)
    checkSolution("Day 17 Part 2 Example", day17Part2Solution("inputs/day17/part1.txt"))
}

enum class PathDirection {Left, Right, Up, Down}


data class CityBlockNode(val row: Int, val col: Int, val heatLoss: Long, val direction: PathDirection, val directionCounter: Int, val parent: CityBlockNode?) {
    fun key(): String {
        return "$row:$col:$direction:$directionCounter"
    }
}

// Need to create a bucket for each block, direction, and directionCounter that I can put paths in.
class CityBlock(val heatLoss: Int, val row: Int, val col: Int)
class Day17(input: String) {
    private val cityBlocks = File(input).readLines().mapIndexed {rowIndex, row ->
        row.mapIndexed { blockIndex, block ->
            CityBlock(block.digitToInt(), rowIndex, blockIndex)
        }
    }
    private val rows = cityBlocks.size
    private val cols = cityBlocks[0].size
    private val blockNodes: PriorityQueue<CityBlockNode> = PriorityQueue<CityBlockNode>(compareBy { it.heatLoss })
    private val nodesVisited = mutableSetOf<String>()


    private fun getNeighbor(node: CityBlockNode, direction: PathDirection, minSteps: Int = 1, maxSteps: Int = 3): CityBlockNode? {
        if(
            node.directionCounter != 0 && (
            node.direction == PathDirection.Up && direction == PathDirection.Down ||
            node.direction == PathDirection.Down && direction == PathDirection.Up ||
            node.direction == PathDirection.Left && direction == PathDirection.Right ||
            node.direction == PathDirection.Right && direction == PathDirection.Left ||
            (node.direction == direction && node.directionCounter >= maxSteps) ||
            (node.direction != direction && node.directionCounter < minSteps))) {
            return null
        }
        val index = when(direction) {
            PathDirection.Up -> Pair(node.row - 1, node.col)
            PathDirection.Down -> Pair(node.row + 1, node.col)
            PathDirection.Left -> Pair(node.row, node.col - 1)
            PathDirection.Right -> Pair(node.row, node.col + 1)
        }
        if(index.first < 0 || index.first >= rows || index.second < 0 || index.second >= cols) {
            return null
        }
        return CityBlockNode(
            index.first,
            index.second,
            node.heatLoss + cityBlocks[index.first][index.second].heatLoss.toLong(),
            direction,
            if(node.direction == direction) { node.directionCounter + 1 } else { 1 },
            node
        )
    }

    fun minHeatLoss2(minSteps: Int, maxSteps: Int): Long {
        blockNodes.add(CityBlockNode(0, 0, 0, PathDirection.Up, 0, null))

        while(blockNodes.isNotEmpty()) {
            val node = blockNodes.poll()
            if(node.row == rows - 1 && node.col == cols - 1) {
                val blockCopy = cityBlocks.map { row ->
                    row.map { block ->
                        block.heatLoss.toString()
                    }.toMutableList()
                }.toMutableList()
                var nextNode = node
                while(nextNode != null) {
                    blockCopy[nextNode.row][nextNode.col] = when (nextNode.direction) {
                        PathDirection.Up -> "^"
                        PathDirection.Down -> "v"
                        PathDirection.Left -> "<"
                        PathDirection.Right -> ">"
                    }
                    nextNode = nextNode.parent
                }
                println("Heat Loss: ${node.heatLoss}")
                blockCopy.forEach { row ->
                    println(row.joinToString(""))
                }
                println("")

                return node.heatLoss
            }
            PathDirection.entries.forEach { direction ->
                val neighbor = getNeighbor(node, direction, minSteps, maxSteps)
                if(neighbor != null && !nodesVisited.contains(neighbor.key())) {
                    blockNodes.add(neighbor)
                    nodesVisited.add(neighbor.key())
                }
            }
        }
        return 0
    }

}

fun day17Part1Solution(path: String): Long {
    val problem = Day17(path)
    val heatLoss = problem.minHeatLoss2(1, 3)
    return heatLoss
}


fun day17Part2Solution(path: String): Long {
    val problem = Day17(path)
    val heatLoss = problem.minHeatLoss2(4, 10)
    return heatLoss
}