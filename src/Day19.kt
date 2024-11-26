import java.io.File

fun main() {
    checkSolution("Day 19 Part 1 Example", day19Part1Solution("inputs/day19/exampleWorkflows.txt", "inputs/day19/exampleParts.txt"), 19114)
    checkSolution("Day 19 Part 1", day19Part1Solution("inputs/day19/inputWorkflows.txt", "inputs/day19/inputParts.txt"), 263678)
    checkSolution("Day 19 Part 2 Example", day19Part2Solution("inputs/day19/exampleWorkflows.txt", "inputs/day19/exampleParts.txt"), 167409079868000)
    checkSolution("Day 19 Part 2", day19Part2Solution("inputs/day19/inputWorkflows.txt", "inputs/day19/inputParts.txt"))
}

enum class Day19PartProperties {
    COOLNESS,
    MUSICALITY,
    AERODYNAMIC,
    SHINY
}


class XmasRange(
    val instruction: IWorkflowInstruction,
    val xmasRange: Map<Day19PartProperties, IntRange> = mapOf(
        Day19PartProperties.COOLNESS to 1 .. 4000,
        Day19PartProperties.MUSICALITY to 1 .. 4000,
        Day19PartProperties.AERODYNAMIC to 1 .. 4000,
        Day19PartProperties.SHINY to 1 .. 4000))
{

}
class Workflow(val name: String, val instructions: List<IWorkflowInstruction>) {
    /*
    fun getWorkflowResult(part: Day19Part): String {
        return instructions.first { it.getInstructionResult(part).isNotEmpty() }.result
    }

     */
}

enum class InstructionType {
    ACCEPT,
    REJECT,
    EXECUTE
}
interface IWorkflowInstruction {
    val instructionType: InstructionType
    val result: String
    fun linkInstruction(workflows: List<Workflow>, nextOnFalse: IWorkflowInstruction?)
    fun execute(part: Day19Part): IWorkflowInstruction

    fun executePart2(range: XmasRange): List<XmasRange>
}


private fun getWorkflowInstruction(workflows: List<Workflow>, action: String): IWorkflowInstruction {
    return when(action) {
        "A" -> AcceptInstruction()
        "R" -> RejectInstruction()
        else -> workflows.find { it.name == action }!!.instructions.first()
    }
}

class AcceptInstruction: IWorkflowInstruction {
    override val instructionType = InstructionType.ACCEPT
    override val result = "A"
    override fun linkInstruction(workflows: List<Workflow>, nextOnFalse: IWorkflowInstruction?) {
    }
    override fun execute(part: Day19Part): IWorkflowInstruction { return this }

    override fun executePart2(range: XmasRange): List<XmasRange> {
        return listOf(range)
    }
}

class RejectInstruction: IWorkflowInstruction {
    override val instructionType = InstructionType.REJECT
    override val result = "R"
    override fun linkInstruction(workflows: List<Workflow>, nextOnFalse: IWorkflowInstruction?) {
    }
    override fun execute(part: Day19Part): IWorkflowInstruction { return this }
    override fun executePart2(range: XmasRange): List<XmasRange> {
        return listOf(range)
    }
}

class LessThanInstruction(val property: Day19PartProperties, val value: Int, override val result: String): IWorkflowInstruction {
    override val instructionType = InstructionType.EXECUTE
    private var instructionIfTrue: IWorkflowInstruction = RejectInstruction()
    private var instructionIfFalse: IWorkflowInstruction = RejectInstruction()
    override fun linkInstruction(workflows: List<Workflow>, nextOnFalse: IWorkflowInstruction?) {

        instructionIfTrue = getWorkflowInstruction(workflows, result)
        instructionIfFalse = nextOnFalse!!
    }
    override fun execute(part: Day19Part): IWorkflowInstruction { return if(part.properties[property]!! < value) instructionIfTrue else instructionIfFalse }
    override fun executePart2(range: XmasRange): List<XmasRange> {
        return if(value > range.xmasRange[property]!!.last) {
            listOf(XmasRange(instructionIfTrue, range.xmasRange))
        }
        else if(value < range.xmasRange[property]!!.first) {
            listOf(XmasRange(instructionIfFalse, range.xmasRange))
        }
        else {
            val xmasTrue = range.xmasRange.toMutableMap()
            xmasTrue[property] = range.xmasRange[property]!!.first until value
            val xmasFalse = range.xmasRange.toMutableMap()
            xmasFalse[property] = value .. range.xmasRange[property]!!.last
            listOf(XmasRange(instructionIfTrue, xmasTrue), XmasRange(instructionIfFalse, xmasFalse))
        }
    }
}


class GreaterThanInstruction(private val property: Day19PartProperties, val value: Int, override val result: String): IWorkflowInstruction {
    override val instructionType = InstructionType.EXECUTE
    private var instructionIfTrue: IWorkflowInstruction = RejectInstruction()
    private var instructionIfFalse: IWorkflowInstruction = RejectInstruction()
    override fun linkInstruction(workflows: List<Workflow>, nextOnFalse: IWorkflowInstruction?) {

        instructionIfTrue = getWorkflowInstruction(workflows, result)
        instructionIfFalse = nextOnFalse!!
    }
    override fun execute(part: Day19Part): IWorkflowInstruction { return if(part.properties[property]!! > value) instructionIfTrue else instructionIfFalse }
    override fun executePart2(range: XmasRange): List<XmasRange> {
        return if(value < range.xmasRange[property]!!.first) {
            listOf(XmasRange(instructionIfTrue, range.xmasRange))
        }
        else if(value > range.xmasRange[property]!!.last) {
            listOf(XmasRange(instructionIfFalse, range.xmasRange))
        }
        else {
            val xmasTrue = range.xmasRange.toMutableMap()
            xmasTrue[property] = value + 1 .. range.xmasRange[property]!!.last
            val xmasFalse = range.xmasRange.toMutableMap()
            xmasFalse[property] = range.xmasRange[property]!!.first .. value
            listOf(XmasRange(instructionIfTrue, xmasTrue), XmasRange(instructionIfFalse, xmasFalse))
        }
    }
}


class EqualsInstruction(val property: Day19PartProperties, val value: Int, override val result: String): IWorkflowInstruction {
    override val instructionType = InstructionType.EXECUTE
    private var instructionIfTrue: IWorkflowInstruction = RejectInstruction()
    private var instructionIfFalse: IWorkflowInstruction = RejectInstruction()
    override fun linkInstruction(workflows: List<Workflow>, nextOnFalse: IWorkflowInstruction?) {

        instructionIfTrue = getWorkflowInstruction(workflows, result)
        instructionIfFalse = nextOnFalse!!
    }
    override fun execute(part: Day19Part): IWorkflowInstruction { return if(part.properties[property]!! == value) instructionIfTrue else instructionIfFalse }
    override fun executePart2(range: XmasRange): List<XmasRange> {
        return if(value in range.xmasRange[property]!!) {

            val xmasFalse1 = range.xmasRange.toMutableMap()
            xmasFalse1[property] = range.xmasRange[property]!!.first until value
            val xmasFalse2 = range.xmasRange.toMutableMap()
            xmasFalse2[property] = value + 1 .. range.xmasRange[property]!!.last
            val xmasTrue = range.xmasRange.toMutableMap()
            xmasTrue[property] = value .. value

            listOf(XmasRange(instructionIfTrue, xmasTrue), XmasRange(instructionIfFalse, xmasFalse1), XmasRange(instructionIfFalse, xmasFalse2))
        }
        else {
            listOf(XmasRange(instructionIfFalse, range.xmasRange))
        }
    }
}


class WorkflowInstruction(override val result: String): IWorkflowInstruction {
    override val instructionType = InstructionType.EXECUTE
    private var nextInstruction: IWorkflowInstruction = RejectInstruction()
    override fun linkInstruction(workflows: List<Workflow>, nextOnFalse: IWorkflowInstruction?) {

        nextInstruction = getWorkflowInstruction(workflows, result)
    }

    override fun execute(part: Day19Part): IWorkflowInstruction {
        return nextInstruction
    }

    override fun executePart2(range: XmasRange): List<XmasRange> {
        return listOf(XmasRange(nextInstruction, range.xmasRange))
    }
}

data class Day19Part(val properties: Map<Day19PartProperties, Int>)


class Day19(private val workflowInput: String, private val partInput: String) {

    private val firstInstruction: IWorkflowInstruction
    init {
        val workflows = getWorkflows()
        workflows.forEach { workflow ->
            for(i in 0 until workflow.instructions.size - 1) {
                workflow.instructions[i].linkInstruction(workflows, workflow.instructions[i + 1])
            }
            workflow.instructions.last().linkInstruction(workflows, null)
        }
        firstInstruction = workflows.find { it.name == "in" }!!.instructions.first()
    }


    private fun getWorkflows(): List<Workflow> {
        return File(workflowInput).readLines()
            .map { s ->
                if (s.startsWith("\uFEFF")) {
                    s.substring(1)
                }
                else {
                    s
                }
            }
            .map { line ->
                val instructionStart = line.indexOfFirst { it == '{' }
                val name = line.substring(0, instructionStart)
                val instructions = line.substring(instructionStart + 1, line.length - 1).split(",").map { instruction ->
                    val instructionParts = instruction.split(":")
                    if(instructionParts.size == 2) {
                        val check = instructionParts[0]
                        val value = check.substring(2).toInt()
                        val property = when(check.first()) {
                            'x' -> Day19PartProperties.COOLNESS
                            'm' -> Day19PartProperties.MUSICALITY
                            'a' -> Day19PartProperties.AERODYNAMIC
                            's' -> Day19PartProperties.SHINY
                            else -> Day19PartProperties.SHINY
                        }
                        when(check[1]) {
                            '<' -> LessThanInstruction(property, value, instructionParts[1])
                            '>' -> GreaterThanInstruction(property, value, instructionParts[1])
                            '=' -> EqualsInstruction(property, value, instructionParts[1])
                            else -> EqualsInstruction(property, value, instructionParts[1])
                        }
                    }
                    else {
                        WorkflowInstruction(instruction)
                    }
                }
                Workflow(name, instructions)
            }
    }

    private fun getParts(): List<Day19Part> {
        return File(partInput).readLines()
            .map { s ->
                if (s.startsWith("\uFEFF")) {
                    s.substring(1)
                }
                else {
                    s
                }
            }
            .map { part ->
                val regex = "\\{x=(\\d*),m=(\\d*),a=(\\d*),s=(\\d*)}".toRegex()
                val (x, m, a, s) = regex.find(part)?.destructured ?: error("Error parsing part $part")
                Day19Part(mapOf(
                    Day19PartProperties.COOLNESS to x.toInt(),
                    Day19PartProperties.MUSICALITY to m.toInt(),
                    Day19PartProperties.AERODYNAMIC to a.toInt(),
                    Day19PartProperties.SHINY to s.toInt()))
            }
    }
    fun getSolution(): Long {
        val parts = getParts()

        return parts.sumOf { part ->
            var currentInstruction = firstInstruction
            while(currentInstruction.instructionType == InstructionType.EXECUTE) {
                currentInstruction = currentInstruction.execute(part)
            }
            if(currentInstruction.instructionType == InstructionType.ACCEPT) {
                part.properties.entries.map { it.value }.sum().toLong()
            }
            else {
                0L
            }

        }
    }

    fun getSolution2(): Long {
        val ranges = mutableListOf(XmasRange(firstInstruction))

        val acceptedRanges = mutableListOf<XmasRange>()

        while(ranges.isNotEmpty()) {
            val openRanges = mutableListOf<XmasRange>()
            ranges.forEach { range ->
                range.instruction.executePart2(range).forEach { nextRange ->
                    when(nextRange.instruction.instructionType) {
                        InstructionType.ACCEPT -> acceptedRanges.add(nextRange)
                        InstructionType.REJECT -> {}
                        InstructionType.EXECUTE -> openRanges.add(nextRange)
                    }
                }
            }
            ranges.clear()
            ranges.addAll(openRanges)
        }


        return acceptedRanges.sumOf { range ->
            range.xmasRange[Day19PartProperties.COOLNESS]!!.count().toLong() *
            range.xmasRange[Day19PartProperties.MUSICALITY]!!.count().toLong() *
            range.xmasRange[Day19PartProperties.AERODYNAMIC]!!.count().toLong() *
            range.xmasRange[Day19PartProperties.SHINY]!!.count().toLong()
        }
    }
}

fun day19Part1Solution(workflows: String, parts: String): Long {
    return Day19(workflows, parts).getSolution()
}


fun day19Part2Solution(workflows: String, parts: String): Long {
    return Day19(workflows, parts).getSolution2()
}