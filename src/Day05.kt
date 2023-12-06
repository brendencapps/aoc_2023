import java.io.File

fun main() {
    checkSolution("Day 5 Part 1 Example", day5Part1Solution("inputs/day05/example.txt"), 35.toLong())
    checkSolution("Day 5 Part 1", day5Part1Solution("inputs/day05/part1.txt"), 199602917)
    checkSolution("Day 5 Part 2 Example", day5Part2Solution("inputs/day05/example.txt"), 46)
    checkSolution("Day 5 Part 2", day5Part2Solution("inputs/day05/part1.txt"), 2254686)
}

fun day5Part1Solution(path: String): Long {
    var inputs = listOf<Long>()
    val maps = mutableListOf<SeedMapping>()
    File(path).readLines().forEach { line ->
        if (line.startsWith("seeds: ")) {
            inputs = (line.substring(line.indexOf(": ") + 2).split(" ").map { it.toLong() })
        } else if (line.isBlank() && maps.isNotEmpty()) {
            val newInputs = inputs.map { input ->
                val map = maps.firstOrNull { map -> map.source <= input && input < map.source + map.length }
                if (map == null) {
                    input
                } else {
                    (input - map.source) + map.destination
                }
            }
            inputs = newInputs
            maps.clear()
        } else if (line.isNotBlank() && !line.contains(":")) {
            val parts = line.split(" ")
            maps.add(SeedMapping(destination = parts[0].toLong(), source = parts[1].toLong(), length = parts[2].toLong()))
        }
    }

    return inputs.min()
}

class SeedMapping(val source: Long, val destination: Long, val length: Long) {
    val sourceRange = source until source + length
    val destinationRange = destination until destination + length

    override fun toString(): String {
        return "[$source, $destination, $length]"
    }
}
class SeedMapTree {
    val nodes = mutableListOf<SeedMapping>()

    fun mergeMapping(map: MutableList<SeedMapping>) {
        val newNodes = mutableListOf<SeedMapping>()
        map.forEach { newMap ->
            val oldNodes = mutableListOf<SeedMapping>()
            nodes.forEach { node ->

                if(newMap.sourceRange.first <= node.destinationRange.first && newMap.sourceRange.last >= node.destinationRange.first) {
                    newNodes.add(SeedMapping(
                        source = node.source,
                        destination = newMap.destination + node.destinationRange.first - newMap.sourceRange.first,
                        length = minOf(node.destinationRange.last, newMap.sourceRange.last) - node.destinationRange.first + 1
                    ))
                    if(newMap.sourceRange.last < node.destinationRange.last) {
                        val remainder = node.destinationRange.last - newMap.sourceRange.last
                        oldNodes.add(SeedMapping(node.source + node.length - remainder, node.destination + node.length - remainder, remainder))

                    }
                }
                else if(node.destinationRange.contains(newMap.sourceRange.first)) {

                    val prefixLength = newMap.sourceRange.first - node.source
                    oldNodes.add(SeedMapping(node.source, node.destination, prefixLength))
                    newNodes.add(SeedMapping(
                        source = node.source + prefixLength,
                        destination = newMap.destination,
                        length = minOf(node.destinationRange.last, newMap.sourceRange.last) - node.destinationRange.first + 1
                    ))
                    if(newMap.sourceRange.last < node.destinationRange.last) {
                        val remainder = node.destinationRange.last - newMap.sourceRange.last
                        oldNodes.add(SeedMapping(node.source + node.length - remainder, node.destination + node.length - remainder, remainder))

                    }
                }
                else {
                    oldNodes.add(node)
                }
            }
            nodes.clear()
            nodes.addAll(oldNodes)
        }
        nodes.addAll(newNodes)
    }

    fun getMin(): Long {
        return nodes.minOf { it.destination }
    }
}

fun day5Part2Solution(path: String): Long {
    val mapTree = SeedMapTree()
    val currentMapping = mutableListOf<SeedMapping>()
    val initialNodes = mutableListOf<SeedMapping>()

    File(path).readLines().forEach { line ->
        if (line.startsWith("seeds: ")) {
            val inputs = (line.substring(line.indexOf(": ") + 2).split(" ").map { it.toLong() })
            for(i in 0 until inputs.size / 2) {
                mapTree.nodes.add(SeedMapping(inputs[i*2], inputs[i*2], inputs[i*2+1]))
                initialNodes.add(SeedMapping(inputs[i*2], inputs[i*2], inputs[i*2+1]))
            }
        } else if (line.contains(":")) {
            if(currentMapping.isNotEmpty()) {
                mapTree.mergeMapping(currentMapping)
            }
            currentMapping.clear()
        } else if (line.isNotBlank()) {
            val parts = line.split(" ")
            currentMapping.add(SeedMapping(destination = parts[0].toLong(), source = parts[1].toLong(), length = parts[2].toLong()))
        }
    }
    mapTree.mergeMapping(currentMapping)
    return mapTree.getMin()

}


