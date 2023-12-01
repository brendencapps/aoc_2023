
fun <T>checkSolution(label: String, result: T, expectedResult: T? = null) {
    if(expectedResult == null) {
        println("$label: $result")
    }
    else {
        if(result != expectedResult) {
            println("$label: Wrong solution $expectedResult != $result")
        }
        check(expectedResult == result)
    }
}