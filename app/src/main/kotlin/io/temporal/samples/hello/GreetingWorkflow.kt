package io.temporal.samples.hello

import io.temporal.activity.Activity
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.workflow.Async
import io.temporal.workflow.Promise
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.time.Duration
import java.time.Instant
import java.time.ZoneId


@WorkflowInterface
interface GreetingWorkflow {
    @WorkflowMethod
    fun greeting(name: String): String
}

class GreetingWorkflowImpl : GreetingWorkflow {
    private fun getActivities() = Workflow.newActivityStub(
        GreetingActivities::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setRetryOptions(
                RetryOptions.newBuilder().setMaximumAttempts(1).build()
            )
            .build()
    )

    override fun greeting(name: String): String {
        Workflow.getLogger(GreetingWorkflow::class.java).info(
            "Starting greeting at ${
                Instant.ofEpochMilli(Workflow.currentTimeMillis()).atZone(ZoneId.systemDefault())
            }"
        )

        listOf(1, 2, 3, 4, 5).map { request ->
            Async.procedure(getActivities()::composeGreeting, "hello", name + request)
        }
            .let { asyncTasks ->
                Workflow.getLogger("logger").info("Waiting for ${asyncTasks.size} activities to generate documents")
                Promise.allOf(asyncTasks).get()
            }

        Workflow.sleep(Duration.ofSeconds(5))
        return getActivities().composeGreeting("hello", name)

    }
}


open class GreetingActivitiesImpl : GreetingActivities {
    override fun composeGreeting(greeting: String, name: String): String {
        println("Greeting started: $greeting")
        return "$greeting, $name!"
    }

    override fun test(): String {
        println("Greeting started")

        return "Hello"
    }
}

@ActivityInterface
interface GreetingActivities {
    @ActivityMethod(name = "greet")
    fun composeGreeting(greeting: String, name: String): String

    @ActivityMethod(name = "greet1")
    fun test(): String
}




