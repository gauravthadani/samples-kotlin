package io.temporal.samples.update_nde

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import io.temporal.client.newWorkflowStub
import io.temporal.samples.QueueDepthSample.localClient
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

        if (mode == "activityWorker") {
            activityWorker()
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


fun runActivityWorker() {
    println("Running Worker")
    activityWorker()
}

fun runStarter() {
    println("Running Starter")
    val id = starter()
//    runSignaller(id)

}

fun runSignaller(wf: String) {
    println("Running Signaller")

    val stub = localClient(namespace = "gaurav-mrn.a2dd6").newWorkflowStub<GreetingWorkflow>(wf)

    runBlocking {
        (1..10).map { i ->
            launch(Dispatchers.IO) {
                stub.notify("val$i")
            }
        }.joinAll()
    }
}