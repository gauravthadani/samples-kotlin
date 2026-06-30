package io.temporal.samples.cube_nde

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt

class Hello : CliktCommand() {
    val mode: String by option().prompt("Mode").help("worker or starter or signaller")
    override fun run() {
        if (mode == "worker") runWorker()
        if (mode == "starter") runStarter()
    }
}

fun main(args: Array<String>) = Hello().main(args)
fun runWorker() = worker()
fun runStarter() = starter()
