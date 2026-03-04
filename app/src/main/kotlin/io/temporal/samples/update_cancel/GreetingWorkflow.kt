package io.temporal.samples.update_cancel

import io.temporal.activity.*
import io.temporal.common.RetryOptions
import io.temporal.failure.ActivityFailure
import io.temporal.workflow.UpdateMethod
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime

data class Request(val name: String?, val date: OffsetDateTime?)

data class CancellationStatus(val status: String)

@WorkflowInterface
interface GreetingWorkflow {
    @WorkflowMethod
    fun greeting(name: String): String

    @UpdateMethod
    fun cancel(name: String): CancellationStatus

}

class GreetingWorkflowImpl : GreetingWorkflow {
    private var shouldComplete: Boolean = false
    var logger = Workflow.getLogger(GreetingWorkflow::class.java)

    init {
        logger.info("Workflow is initialized")
    }

    private fun getActivities() = Workflow.newActivityStub(
        GreetingActivities::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(10))
            .setRetryOptions(
                RetryOptions.newBuilder().setMaximumAttempts(1).build()
            )
            .build()
    )

    override fun greeting(name: String): String {
        var result = ""
        try {
            result = getActivities().composeGreeting("hello", name)
        } catch (e: ActivityFailure) {
            shouldComplete = true
            Workflow.newDetachedCancellationScope {
                Workflow.await { Workflow.isEveryHandlerFinished() }
            }.run()
        }

        return result
    }

    override fun cancel(name: String): CancellationStatus {
        Workflow.await { shouldComplete }
        return CancellationStatus("cancelled")
    }
}

open class GreetingActivitiesImpl : GreetingActivities {
    override fun composeGreeting(greeting: String, name: String): String {

        println("Greeting started: $greeting")
        val executionContext = Activity.getExecutionContext()
        (1..500).forEach { _ ->
            executionContext.heartbeat(Instant.now())
            Thread.sleep(500_0)
        }
        return "$greeting, $name!"
    }
}

@ActivityInterface
interface GreetingActivities {
    @ActivityMethod(name = "greet")
    fun composeGreeting(greeting: String, name: String): String
}





