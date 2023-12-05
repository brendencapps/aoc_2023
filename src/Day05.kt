import java.io.File
import java.lang.Long.min

fun main() {

    checkSolution("Day 5 Part 1 Example", day5Part1Solution("inputs/day05/example.txt"), 35.toLong())
    checkSolution("Day 5 Part 1", day5Part1Solution("inputs/day05/part1.txt"), 199602917)
    checkSolution("Day 5 Part 2 Example", day5Part2Solution2("inputs/day05/example.txt"), 46)
    //checkSolution("Day 5 Part 2", day5Part2Solution2("inputs/day05/part1.txt"))
}


fun day5Part1Solution(path: String): Long {
    var inputs = listOf<Long>()
    val maps = mutableListOf<Day5Map>()
    File(path).readLines().forEach { line ->
        if (line.startsWith("seeds: ")) {
            inputs = (line.substring(line.indexOf(": ") + 2).split(" ").map { it.toLong() })
        } else if (line.isBlank() && maps.isNotEmpty()) {
            // Do Mapping
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
            maps.add(Day5Map(destination = parts[0].toLong(), source = parts[1].toLong(), length = parts[2].toLong()))
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
        println(nodes)
        println(map)
        map.forEach { newMap ->
            val newNodes = mutableListOf<SeedMapping>()
            nodes.forEach { node ->
                if(
                    node.destinationRange.contains(newMap.sourceRange.first) ||
                    node.destinationRange.contains(newMap.sourceRange.last) ||
                    newMap.sourceRange.contains(node.destinationRange.first) ||
                    newMap.sourceRange.contains(node.destinationRange.last)) {
                    if(newMap.sourceRange.first <= node.destinationRange.first) {
                        val offset = node.destinationRange.first - newMap.sourceRange.first
                        val endOfRange = minOf(newMap.sourceRange.last, node.destinationRange.last)
                        val length = endOfRange - node.destinationRange.first + 1
                        newNodes.add(SeedMapping(node.sourceRange.first, newMap.destinationRange.first + offset, length))
                        if(length < node.length) {
                            newNodes.add(SeedMapping(node.sourceRange.first + offset, node.destinationRange.first + offset, node.length - length))
                        }
                    }
                    else {
                        val prefixLength = newMap.sourceRange.first - node.destinationRange.first + 1
                        val newMapLength = minOf(newMap.length, node.length - prefixLength)
                        val suffixLength = node.length - newMapLength - prefixLength
                        newNodes.add(SeedMapping(node.source, node.destination, prefixLength))
                        newNodes.add(SeedMapping(node.source + prefixLength, newMap.destination, newMapLength))
                        if(suffixLength > 0) {
                            newNodes.add(SeedMapping(node.source + prefixLength + newMapLength, node.destination + prefixLength + newMapLength, suffixLength))
                        }
                    }
                }
                else {
                    newNodes.add(node)
                }
            }
            nodes.clear()
            nodes.addAll(newNodes)
        }
    }

    fun getMin(): Long {
        return nodes.minOf { it.destination }
    }
}

class Day5Map(val destination: Long, val source: Long, val length: Long) {
    fun intersects(other: Day5Map): Boolean {
        val maxx = maxOf(other.source, destination)
        val minn = minOf(other.source + other.length, destination + length)
        //println("$maxx $minn $other $this")
        return maxOf(other.source, destination) < minOf(other.source + other.length, destination + length )
    }

    fun destinationRange(): LongRange {
        return destination until destination + length
    }
    fun sourceRange(): LongRange {
        return source until source + length
    }

    override fun toString(): String {
        return "[$source, $destination, $length]"
    }

}

fun combineMaps(originalMap: List<Day5Map>, newMap: List<Day5Map>): List<Day5Map> {


    val mapResult = mutableListOf<Day5Map>()
    mapResult.addAll(originalMap)
    newMap.forEach { new ->
        val resultCopy = mapResult.toMutableList()
        mapResult.clear()
        resultCopy.forEach { original ->
            //println("original $original, new $new")
            if(original.intersects(new)) {
                //println("intersection")
                if(new.source < original.destination) {
                    val offset = original.destination - new.source
                    val length = min(original.length, new.length - offset)
                    //println("merge $original $new")
                    //println("$offset ${original.length} ${new.length} $length")
                    val mapToAdd = Day5Map(
                            source = original.source,
                            destination = new.destination + offset,
                            length = length)
                    //println("Adding source <= $mapToAdd")
                    mapResult.add(mapToAdd)
                    if(length < original.length) {
                        val mapToAdd2 = Day5Map(
                                source = original.source + length,
                                destination = original.destination + length,
                                length = original.length - length)
                        //println("Adding remaining <= $mapToAdd2")
                        mapResult.add(mapToAdd2)
                    }
                }
                else {
                    val offset = new.source - original.destination
                    val length = min(original.length - offset, new.length)
                    var mapToAdd = Day5Map(
                            source = original.source,
                            destination = original.destination,
                            length = original.length - offset)
                    //println("Adding first part original <= $mapToAdd")
                    mapResult.add(mapToAdd)
                    mapToAdd = Day5Map(
                            source = original.source + offset,
                            destination = new.destination,
                            length = length)
                    //println("Adding new <= $mapToAdd")
                    mapResult.add(mapToAdd)
                    if(length < original.length - offset) {
                        mapToAdd = Day5Map(
                                source = original.source + length + offset,
                                destination = original.destination + length + offset,
                                length = original.length - offset - length)
                        //println("Adding last part of original <= $mapToAdd")
                        mapResult.add(mapToAdd)
                    }
                }
            }
            else {
                mapResult.add(original)
            }
        }
    }
    return mapResult

}

fun mergeMapping(mapTree: SeedMapTree, map: MutableList<SeedMapping> ) {

}
fun day5Part2Solution2(path: String): Long {
    val mapTree = SeedMapTree()
    val currentMapping = mutableListOf<SeedMapping>()

    File(path).readLines().forEach { line ->
        if (line.startsWith("seeds: ")) {
            val inputs = (line.substring(line.indexOf(": ") + 2).split(" ").map { it.toLong() })
            for(i in 0 until inputs.size / 2) {
                mapTree.nodes.add(SeedMapping(inputs[i*2], inputs[i*2], inputs[i*2+1]))
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

fun day5Part2Solution(path: String): Long {
    var currentMap = mutableListOf<Day5Map>()
    val maps = mutableListOf<MutableList<Day5Map>>()
    File(path).readLines().forEach { line ->
        if (line.startsWith("seeds: ")) {
            val inputs = (line.substring(line.indexOf(": ") + 2).split(" ").map { it.toLong() })
            for(i in 0 until inputs.size / 2) {
                currentMap.add(Day5Map(source = inputs[i*2], destination = inputs[i*2], length = inputs[i*2+1]))
            }
            maps.add(currentMap)
        } else if (line.contains(":")) {
            currentMap = mutableListOf<Day5Map>()
            maps.add(currentMap)
        } else if (line.isNotBlank()) {
            val parts = line.split(" ")
            currentMap.add(Day5Map(destination = parts[0].toLong(), source = parts[1].toLong(), length = parts[2].toLong()))
        }
    }


    var map:List<Day5Map> = maps[0]
    println(map)
    for(i in 1 until maps.size) {

        println("merge ${maps[i]}")
        map = combineMaps(map, maps[i])
        println("result $map")
    }
    return map.minOf { it.destination }



}

