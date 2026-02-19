package io.temporal.samples.Schedule

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main


class Hello : CliktCommand() {
//    val mode: String by option().prompt("Mode").help("worker or starter")

    override fun run() {
        println("Schedule")
        worker()
    }
}

fun main(args: Array<String>) = Hello().main(args)


