package io.temporal.samples.springboot.hello

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.spring.boot.ActivityImpl
import io.temporal.spring.boot.WorkflowImpl
import io.temporal.workflow.Async
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

@WorkflowInterface
interface GreetingWorkflow {
    @WorkflowMethod
    fun greeting(name: String): String
}

@WorkflowImpl(taskQueues = ["HelloSampleTaskQueue"])
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
        println("THREAD INFO AFTER: ${Thread.currentThread().name} - ${Thread.currentThread().contextClassLoader}")
        Workflow.getLogger(GreetingWorkflow::class.java).info(
            "Starting greeting at ${
                Instant.ofEpochMilli(Workflow.currentTimeMillis()).atZone(ZoneId.systemDefault())
            }"
        )


        listOf(1, 2, 3, 4, 5).map {
            Async.procedure {
                getActivities().composeGreeting("hello + $it", name)
            }
        }

        return getActivities().composeGreeting("hello", name)

    }
}

@Component
@ActivityImpl(taskQueues = ["HelloSampleTaskQueue"])
open class GreetingActivitiesImpl : GreetingActivities {
    override fun composeGreeting(greeting: String, name: String): String {
        println("Greeting started: $greeting")
        return "$greeting, $name!"
    }
}

@ActivityInterface
interface GreetingActivities {
    fun composeGreeting(greeting: String, name: String): String
}




