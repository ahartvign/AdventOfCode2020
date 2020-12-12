package day1

import java.io.File

class Day1(inputFilePath: String, private val expectedSum: Int, private val amountOfNumbers: Int) {
    companion object {
        fun exerciseOne() {
            Day1("./src/main/kotlin/day1/realInput.txt", 2020, 3)
                .findAndPrintSummingNumbers()
        }
    }

    private val allNumbers: List<Int> = File(inputFilePath).readLines().map { it.toInt() }
    private val calculatedNumbers = HashSet<Int>()

    private fun findAndPrintSummingNumbers() {
        for (i in allNumbers.indices) {
            val topLevelNumber = allNumbers[i]

            if (calculatedNumbers.contains(topLevelNumber)) continue

            if (addNumbersUntillExpectedResult(listOf(topLevelNumber), topLevelNumber, 1, i + 1)) return

            calculatedNumbers.add(topLevelNumber)
        }

        println("There was no solution")
    }

    private fun addNumbersUntillExpectedResult(
        numbersAddedUp: List<Int>,
        currentSum: Int,
        currentIteration: Int,
        currentIndex: Int
    ): Boolean {
        if (currentIteration == amountOfNumbers) {
            return if (currentSum == expectedSum) {
                val result: Long = numbersAddedUp.map { it.toLong() }.reduce { sum, element -> element * sum }
                println("Adding $numbersAddedUp together gives $currentSum - multiplying those numbers gives $result")
                true
            } else {
                false
            }
        }

        if (currentIndex == allNumbers.size) {
            return false
        }

        val currentNumber = allNumbers[currentIndex]

        val newNumbersAddedUp = numbersAddedUp.toMutableList()
        newNumbersAddedUp.add(currentNumber)

        val valueFound = addNumbersUntillExpectedResult(
            newNumbersAddedUp,
            currentSum + currentNumber,
            currentIteration + 1,
            currentIndex + 1
        )

        return if (valueFound) {
            true
        } else {
            addNumbersUntillExpectedResult(
                numbersAddedUp,
                currentSum,
                currentIteration,
                currentIndex + 1
            )
        }
    }
}
