package day2

import java.io.File

class Day2(inputFilePath: String) {
    companion object {
        fun exerciseTwo() {
            Day2("./src/main/kotlin/day2/realInput.txt").part2()
        }
    }

    data class PasswordLine(val amountRange: IntRange, val letter: Char, val password: String)

    private val allLines: List<PasswordLine> = File(inputFilePath).readLines().map { translateLine(it) }

    private fun translateLine(line: String): PasswordLine {
        val lineSplit = line.split(' ')
        val range = lineSplit[0].split('-').map { it.toInt() }

        return PasswordLine(IntRange(range[0], range[1]), lineSplit[1].removeSuffix(":").toCharArray()[0], lineSplit[2])
    }

    private fun part1() {
        val validPasswords =
            allLines.count { it.amountRange.contains(it.password.count { letter -> it.letter == (letter) }) }
        println("The amount of valid passwords are $validPasswords")
    }

    private fun part2() {
        val validPasswords = allLines.count {
            val firstLetter = it.password[it.amountRange.first - 1]
            val secondLetter = it.password[it.amountRange.last - 1]

            (it.letter == firstLetter && it.letter != secondLetter) || (it.letter != firstLetter && it.letter == secondLetter)
        }

        println("The amount of valid passwords are $validPasswords")
    }
}