import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.math.pow

fun main() {

    checkSolution("Day 5 Part 1 Example", day5Part1Solution("inputs/day05/example.txt"), 35.toLong())
    checkSolution("Day 5 Part 1", day5Part1Solution("inputs/day05/part1.txt"), 199602917)
    checkSolution("Day 5 Part 2 Example", day5Part2Solution("inputs/day05/example.txt"), 46)
    checkSolution("Day 5 Part 2", day5Part2Solution("inputs/day05/part1.txt"))
}

class Day5Map(val destination: Long, val source: Long, val length: Long)

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
            maps.add(Day5Map(parts[0].toLong(), parts[1].toLong(), parts[2].toLong()))
        }
    }

    return inputs.min()
}

fun day5Part2Solution(path: String): Long {
    var inputs = listOf<Long>()
    val maps = mutableListOf<MutableList<Day5Map>>()
    var currentMap = mutableListOf<Day5Map>()
    File(path).readLines().forEach { line ->
        if (line.startsWith("seeds: ")) {
            inputs = (line.substring(line.indexOf(": ") + 2).split(" ").map { it.toLong() })
        } else if (line.contains(":")) {
            currentMap = mutableListOf()
            maps.add(currentMap)
        } else if (line.isNotBlank()) {
            val parts = line.split(" ")
            currentMap.add(Day5Map(parts[0].toLong(), parts[1].toLong(), parts[2].toLong()))
        }
    }

    return runBlocking {

        val resultChannel = Channel<Long>(1000, BufferOverflow.DROP_OLDEST)
        val finishedChannel = Channel<Long>()
        launch {
            var minLocation = (-1).toLong()
            resultChannel.receiveAsFlow().collect {
                if (it == (-1).toLong()) {
                    finishedChannel.send(minLocation)
                }
                else if (minLocation == (-1).toLong() || it < minLocation) {
                    println("Updating minLocation $it")
                    minLocation = it
                }
            }
        }
        launch {
            val jobs = mutableListOf<Job>()
            for (i in 0 until inputs.size / 2) {
                jobs.add(launch {
                    for (j in 0 until inputs[i * 2 + 1]) {

                        var mapInput = inputs[i * 2] + j
                        for (map in maps) {
                            val mapInRange =
                                map.firstOrNull { m -> m.source <= mapInput && mapInput < m.source + m.length }
                            mapInput = if (mapInRange == null) {
                                mapInput
                            } else {
                                (mapInput - mapInRange.source) + mapInRange.destination
                            }
                        }
                        println("Sending $mapInput")
                        resultChannel.send(mapInput)
                    }
                })
            }
            jobs.forEach { it.join() }
            resultChannel.send(-1)
            resultChannel.close()
        }
        println("Wait for result")
        val result = finishedChannel.receive()
        println("Got result $result")
        result
    }
}

