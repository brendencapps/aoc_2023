import java.io.File

fun main() {
    val instructionsExample1 = "RL"
    val instructionsInput1 = "LRRLRRRLRRLLLRLLRRLRRLLRRRLRRLLRLRRRLRLRRLRLRRRLRLRLRRLLRLRLRRLRRRLRRRLRRRLRLRRLLLLRLLRLLRRLRRRLLLRLRRRLRLRRRLRLRRLRRRLRRRLRLRLLRRRLLRLLRLRLRLRLLRRLRRLRRRLRRLRLRLRLRLRRLRRRLLRRRLLRLLLRRRLLRRRLRRRLRRLRLRRLRLLRRLLRRLRLRLRRLRLRRLLRRRLLRRRLLRLRRRLRLRRRLRLRRRLRRRLRRLRRLRRLLRRRLRRRLLLRRRR"

    val instructionsExample2 = "LR"
    checkSolution("Day 8 Part 1 Example", day8Part1Solution("inputs/day08/example.txt", instructionsExample1), 2)
    checkSolution("Day 8 Part 1", day8Part1Solution("inputs/day08/part1.txt", instructionsInput1), 13301)
    checkSolution("Day 8 Part 2 Example", day8Part2Solution("inputs/day08/example2.txt", instructionsExample2), 6)
    checkSolution("Day 8 Part 2", day8Part2Solution("inputs/day08/part1.txt", instructionsInput1), 7309459565207)
}

class DessertMapNode(val name: String, private val left: String, private val right: String) {
    lateinit var leftNode: DessertMapNode
    lateinit var rightNode: DessertMapNode

    fun findLinkedNodes(nodes: List<DessertMapNode>) {
        leftNode = nodes.first { it.name == left }
        rightNode = nodes.first { it.name == right }
    }
}

fun getNodes(path: String): List<DessertMapNode> {
    val nodes = File(path).readLines().map { node ->
        val nodeRegex = "(\\S*) = \\((\\S*), (\\S*)\\)".toRegex()
        val (name, left, right) = nodeRegex.find(node)?.destructured ?: error("Error parsing game $node")

        DessertMapNode(name, left, right)
    }
    nodes.forEach { it.findLinkedNodes(nodes) }
    return nodes
}


fun day8Part1Solution(path: String, instructions: String): Long {

    val nodes = getNodes(path)
    return minLengthToEnd(nodes.first{ it.name == "AAA" }, instructions) {
        it.name == "ZZZ"
    }
}

fun day8Part2Solution(path: String, instructions: String): Long {
    val nodes = getNodes(path)
    val minLengthToEnd = nodes.filter { it.name.endsWith("A") }.map { minLengthToEnd(it, instructions) { node -> node.name.endsWith("Z")} }
    return findLeastCommonMultiple(minLengthToEnd)
}

fun findLeastCommonMultiple(numbers: List<Long>): Long {
    val biggestNumber = numbers.max()
    var lcm = biggestNumber
    while(lcm > 0) {
        if(numbers.all { lcm % it == 0.toLong() }) {
            return lcm
        }
        lcm += biggestNumber
    }
    return 0
}

fun minLengthToEnd(start: DessertMapNode, instructions: String, end: (DessertMapNode) -> Boolean): Long {
    var currentNode = start
    var index = 0
    var length = 0.toLong()
    while(!end(currentNode)) {
        currentNode = if(instructions[index] == 'L') {
            currentNode.leftNode
        }
        else {
            currentNode.rightNode
        }
        index = (index + 1) % instructions.length
        length++
    }
    return length
}
