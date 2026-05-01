package io.temporal.samples.update_nde

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import io.temporal.activity.ActivityOptions
import io.temporal.activity.LocalActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.common.SearchAttributeKey
import io.temporal.workflow.Async
import io.temporal.workflow.SignalMethod
import io.temporal.workflow.UpdateMethod
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*


data class Request(val name: String?, val date: OffsetDateTime?)

@WorkflowInterface
interface GreetingWorkflow {
    @WorkflowMethod
    fun greeting(name: String): String

    @UpdateMethod
    fun notify(name: String): String

    @SignalMethod
    fun ToProceed(name: String)

}

class GreetingWorkflowImpl : GreetingWorkflow {
    private var pleaseProceed: Boolean = false
    var logger = Workflow.getLogger(GreetingWorkflow::class.java)

    init {
        logger.info("Workflow is initialized")

        val slaVersion = Workflow.getVersion("sla-timer", 0, 1)
        if (slaVersion == 1) {
            Async.procedure {
                Workflow.newTimer(Duration.ofSeconds(60)).thenApply {
                    Workflow.getVersion("sla-breach-reemit-loop", 0, 1)
                    Workflow.getVersion("sla-breached-search-attribute", 0, 1)
                    Workflow.upsertTypedSearchAttributes(SLA_BREACHED.valueSet(true))
                    Workflow.newTimer(Duration.ofSeconds(30)).thenApply {
                    }
                }
                Workflow.getVersion("ESIndexingChange", 0, 2)
            }
        }
    }

    private fun getActivities() = Workflow.newActivityStub(
        GreetingActivities::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(45))
            .setRetryOptions(
                RetryOptions.newBuilder().build()
            )
            .build()
    )

    private fun getLocalActivities() = Workflow.newLocalActivityStub(
        GreetingActivities::class.java,
        LocalActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(45))
            .setRetryOptions(
                RetryOptions.newBuilder().build()
            )
            .build()
    )

    val SLA_BREACHED: SearchAttributeKey<Boolean?> = SearchAttributeKey.forBoolean("sla_breached")

    override fun greeting(name: String): String {
        logger.info("Workflow started")

        Workflow.getVersion("skip-init-upserts", 0, 1)
        getLocalActivities().composeGreeting("IsEligibleForProcessing", name)
        getLocalActivities().composeGreeting("BuildFulfillmentOrder", name)
        getActivities().composeGreeting("sleep30", name)
        Workflow.getVersion("PostPersistLocalActivity", 0, 2)
        val useLocalActivity = Workflow.sideEffect(Boolean::class.java) { false }
        executePostPersistStepsV2(name, useLocalActivity)
        Workflow.await { false }
        return getActivities().composeGreeting("hello", name)
    }

    private fun executePostPersistStepsV2(name: String, useLocalActivity: Boolean) {
        getActivities().composeGreeting("sleep10", name)
    }

    override fun notify(name: String): String {
        logger.info("Signal received: $name")
        Workflow.sideEffect(UUID::class.java) {
            UUID.randomUUID()
        }
        return "works"
    }

    override fun ToProceed(name: String) {

        pleaseProceed = true

    }
}

open class GreetingActivitiesImpl : GreetingActivities {
    override fun composeGreeting(greeting: String, name: String): String {

        if (greeting == "sleep30") {
            Thread.sleep(Duration.ofSeconds(30))
        }
        if (greeting == "sleep10") {
            Thread.sleep(Duration.ofSeconds(10))
        }
        println("Greeting started: $greeting")
        return "$greeting, $name!"
    }
}

@ActivityInterface
interface GreetingActivities {
    @ActivityMethod(name = "greet")
    fun composeGreeting(greeting: String, name: String): String
}


open class GreetingActivitiesLocalImpl : GreetingActivitiesLocal {
    override fun composeGreeting(greeting: String, name: String): String {

        if (greeting == "sleep30") {
            Thread.sleep(Duration.ofSeconds(30))
        }
        println("Greeting started: $greeting")
        return "$greeting, $name!"
    }
}

@ActivityInterface
interface GreetingActivitiesLocal {
    @ActivityMethod(name = "greet")
    fun composeGreeting(greeting: String, name: String): String
}




