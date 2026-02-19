package io.temporal.samples.update_nde

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.workflow.Functions.Func
import io.temporal.workflow.UpdateMethod
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.time.Duration
import java.time.OffsetDateTime
import java.util.UUID

data class Request(val name: String?, val date: OffsetDateTime?)

@WorkflowInterface
interface GreetingWorkflow {
    @WorkflowMethod
    fun greeting(name: String): String

    @UpdateMethod
    fun notify(name: String): String

}

class GreetingWorkflowImpl : GreetingWorkflow {
    var logger = Workflow.getLogger(GreetingWorkflow::class.java)

    init {
        logger.info("Workflow is initialized")
    }

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
        logger.info("Workflow started")

        Workflow.getVersion("ChangeId1", 0, 1)
        Workflow.getVersion("ChangeId2", 0, 1)

        Workflow.await { false }
        return getActivities().composeGreeting("hello", name)

    }

    override fun notify(name: String): String {
        logger.info("Signal received: $name")
        Workflow.sideEffect(UUID::class.java) {
            UUID.randomUUID()
        }
        return "works"
    }
}

open class GreetingActivitiesImpl : GreetingActivities {
    override fun composeGreeting(greeting: String, name: String): String {
        println("Greeting started: $greeting")
        return "$greeting, $name!"
    }
}

@ActivityInterface
interface GreetingActivities {
    @ActivityMethod(name = "greet")
    fun composeGreeting(greeting: String, name: String): String
}




