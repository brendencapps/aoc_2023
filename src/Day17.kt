import java.io.File
import java.lang.Long.min

fun main() {
    checkSolution("Day 17 Part 1 Example", day17Part1Solution("inputs/day17/example.txt"), 102)
    checkSolution("Day 17 Part 1", day17Part1Solution("inputs/day17/part1.txt"))
    //checkSolution("Day 17 Part 2 Example", day17Part2Solution("inputs/day17/example.txt"))
    //checkSolution("Day 17 Part 2 Example", day17Part2Solution("inputs/day17/part1.txt"))
}

enum class PathDirection {Left, Right, Up, Down}
class CityBlock(val heatLoss: Int, val row: Int, val col: Int)
class Day17(input: String) {
    val cityBlocks = File(input).readLines().mapIndexed {rowIndex, row ->
        row.mapIndexed { blockIndex, block ->
            CityBlock(block.digitToInt(), rowIndex, blockIndex)
        }
    }
    val blockScore = cityBlocks.map {row -> row.map { Long.MAX_VALUE }.toMutableList() }.toMutableList()

    val cache = mutableMapOf<String, Long>()

    fun getNextBlockMinHeat(nextBlock: CityBlock, path: List<CityBlock>, newDirection: PathDirection, oldDirection: PathDirection, directionCounter: Int): Long {
        println("getNextBlockMinHeat ${nextBlock.row} ${nextBlock.col} $newDirection, $oldDirection, $directionCounter")
        //if(blockScore[nextBlock.row][nextBlock.col] != Long.MAX_VALUE) {
        //    return blockScore[nextBlock.row][nextBlock.col]
        //}
        val newDirectionCounter = if(newDirection == oldDirection) { directionCounter + 1 } else { 1 }
        val cacheKey = "${nextBlock.row}_${nextBlock.col}_$newDirection$newDirectionCounter"
        if(cache.contains(cacheKey)) {
            return cache[cacheKey]!!
        }
        if(!path.contains(nextBlock) && (newDirection != oldDirection || directionCounter < 3)) {
            val heatLoss = minHeatLoss(nextBlock, path + nextBlock, newDirection, newDirectionCounter)
            if(heatLoss == Long.MAX_VALUE) {
                return Long.MAX_VALUE
            }
            //blockScore[nextBlock.row][nextBlock.col] = min(blockScore[nextBlock.row][nextBlock.col], heatLoss + nextBlock.heatLoss)
            cache[cacheKey] = heatLoss + nextBlock.heatLoss
            return heatLoss + nextBlock.heatLoss
        }
        return Long.MAX_VALUE
    }

    fun minHeatLoss(currentBlock: CityBlock, path: List<CityBlock>, direction: PathDirection, directionCounter: Int): Long {

        println("MinHeatLoss ${currentBlock.row} ${currentBlock.col} $direction $directionCounter")
        if(currentBlock.col == cityBlocks[0].lastIndex && currentBlock.row == cityBlocks.lastIndex) {
            println("Found End")
            return 0.toLong()
        }

        var minHeatLoss = Long.MAX_VALUE
        if(currentBlock.col > 0) {
            val block = cityBlocks[currentBlock.row][currentBlock.col - 1]
            minHeatLoss = min(minHeatLoss, getNextBlockMinHeat(block, path, PathDirection.Left, direction, directionCounter))
        }
        if(currentBlock.col < cityBlocks[0].lastIndex) {
            val block = cityBlocks[currentBlock.row][currentBlock.col + 1]
            minHeatLoss = min(minHeatLoss, getNextBlockMinHeat(block, path, PathDirection.Right, direction, directionCounter))
        }
        if(currentBlock.row > 0) {
            val block = cityBlocks[currentBlock.row - 1][currentBlock.col]
            minHeatLoss = min(minHeatLoss, getNextBlockMinHeat(block, path, PathDirection.Up, direction, directionCounter))
        }
        if(currentBlock.row < cityBlocks.lastIndex) {
            val block = cityBlocks[currentBlock.row + 1][currentBlock.col]
            minHeatLoss = min(minHeatLoss, getNextBlockMinHeat(block, path, PathDirection.Down, direction, directionCounter))
        }
        return minHeatLoss
    }


}

fun day17Part1Solution(path: String): Long {
    val problem = Day17(path)
    val heatLoss = problem.minHeatLoss(problem.cityBlocks.first().first(), listOf(), PathDirection.Up, 0)
    for(i in problem.blockScore.indices) {
        for( j in problem.blockScore[i].indices) {
            print("${problem.blockScore[i][j]}:${problem.cityBlocks[i][j].heatLoss} ")
        }
        println("")
    }
    return heatLoss
}


fun day17Part2Solution(path: String): Long {
    return 0.toLong()
}