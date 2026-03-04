package io.temporal.samples.update_cancel

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import io.temporal.client.newWorkflowStub
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class Hello : CliktCommand() {
    val mode: String by option().prompt("Mode").help("worker or starter or signaller")

    override fun run() {

        if (mode == "worker") {
            runWorker()
        }
        if (mode == "starter") {
            runStarter()
        }
    }
}

fun main(args: Array<String>) = Hello().main(args)


fun runWorker() {
    println("Running Worker")
    worker()
}

fun runStarter() {
    println("Running Starter")
    val id = starter()
//    runSignaller(id)

}

fun runSignaller(wf: String) {
    println("Running Signaller")

    val stub = client(namespace = "gaurav-test.a2dd6").newWorkflowStub<GreetingWorkflow>(wf)

    runBlocking {
        (1..10).map { i ->
            launch(Dispatchers.IO) {
                stub.cancel("val$i")
            }
        }.joinAll()
    }
}