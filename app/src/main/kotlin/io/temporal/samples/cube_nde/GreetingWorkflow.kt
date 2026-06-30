package io.temporal.samples.cube_nde

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.common.SearchAttributeKey
import io.temporal.workflow.Async
import io.temporal.workflow.Promise
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.time.Duration

@WorkflowInterface
interface GreetingWorkflow {
    @WorkflowMethod
    fun greeting(name: String): String
}

class GreetingWorkflowImpl : GreetingWorkflow {
    var logger = Workflow.getLogger(GreetingWorkflow::class.java)

    init {
        logger.info("Workflow is initialized")
    }

    private fun activityStub() = Workflow.newActivityStub(
        MyActivityInterface::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(45))
            .setRetryOptions(RetryOptions.newBuilder().build())
            .build()
    )

    private fun untypedStub() = Workflow.newUntypedActivityStub(
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(45))
            .setRetryOptions(RetryOptions.newBuilder().build())
            .build()
    )

    val WORKFLOW_VERSIONS = SearchAttributeKey.forKeywordList("WorkflowVersions")

    val activities = activityStub()
    private lateinit var workflowName: String

    override fun greeting(name: String): String {
        workflowName = name
        logger.info("Workflow started")
        "interrupt-curing-on-ato-transfer".versionMethod(1)
        "flatten-post-submission".versionMethod(1)
        "most-recent-started-activity-search-attribute-added".versionMethod(1)

        // Sequential phase: events 15-66 in the customer's history. Each call
        // blocks its WT until the activity completes; the interceptor inserts
        // the MostRecentStartedActivity upsert immediately before every schedule.
        activities.ShouldMarkAsRegZ("hello", workflowName)
        activities.SecureAccessDevice("hello", workflowName)
        activities.ShouldWaitForLoanPaused("hello", workflowName)
        activities.ShouldRetrieveClaimComponents("hello", workflowName)
        activities.GetClaimComponents("hello", workflowName)
        activities.SaveRecordsOnInvestigationStart("hello", workflowName)
        activities.PublishEventsOnInvestigationStart("hello", workflowName)
        activities.StartRegulatoryWorkflows("hello", workflowName)

        // Async phase: events 70-86 in the customer's history. The block runs
        // through Async.function so its commands land in a single workflow task
        // with the interleaved upsert → activity-schedule → marker → marker
        // ordering that drives the TMPRL1100 failure.
        executeParallelWorkflows()

        Workflow.await { false }
        return "hello world"
    }

    private fun executeParallelWorkflows() {
        val activityGroups =
            buildList<() -> Unit> {
                add(::resolvedExternallyWorkflow)
            }

        val promises = activityGroups.map { Async.function { it() } }
        Promise.anyOf(promises).get()
    }

    private fun resolvedExternallyWorkflow() {
        untypedStub().executeAsync(
            "IsAdditionalEvidenceCollectionEnabled",
            String::class.java,
            "hello",
            workflowName,
        )
        "flatten-ato-transfer".versionMethod(1)
        "flatten-resolved-externally".versionMethod(1)
        activities.GetResolvedExternallyWorkflowSleepDurationSeconds("hello", workflowName)
    }

    private fun String.versionMethod(version: Int): Int {
        Workflow.getVersion(this, 0, version)
        Workflow.upsertTypedSearchAttributes(WORKFLOW_VERSIONS.valueSet(listOf("0$version-${this}")))
        return version
    }
}

open class ActivityImpl : MyActivityInterface {
    override fun ShouldMarkAsRegZ(greeting: String, name: String): String = "$greeting, $name!"
    override fun SecureAccessDevice(greeting: String, name: String): String = "$greeting, $name!"
    override fun ShouldWaitForLoanPaused(greeting: String, name: String): String = "$greeting, $name!"
    override fun ShouldRetrieveClaimComponents(greeting: String, name: String): String = "$greeting, $name!"
    override fun GetClaimComponents(greeting: String, name: String): String = "$greeting, $name!"
    override fun SaveRecordsOnInvestigationStart(greeting: String, name: String): String = "$greeting, $name!"
    override fun PublishEventsOnInvestigationStart(greeting: String, name: String): String = "$greeting, $name!"
    override fun StartRegulatoryWorkflows(greeting: String, name: String): String = "$greeting, $name!"
    override fun IsAdditionalEvidenceCollectionEnabled(greeting: String, name: String): String = "$greeting, $name!"
    override fun GetResolvedExternallyWorkflowSleepDurationSeconds(greeting: String, name: String): String =
        "$greeting, $name!"
}

@ActivityInterface
interface MyActivityInterface {
    fun ShouldMarkAsRegZ(greeting: String, name: String): String
    fun SecureAccessDevice(greeting: String, name: String): String
    fun ShouldWaitForLoanPaused(greeting: String, name: String): String
    fun ShouldRetrieveClaimComponents(greeting: String, name: String): String
    fun GetClaimComponents(greeting: String, name: String): String
    fun SaveRecordsOnInvestigationStart(greeting: String, name: String): String
    fun PublishEventsOnInvestigationStart(greeting: String, name: String): String
    fun StartRegulatoryWorkflows(greeting: String, name: String): String
    fun IsAdditionalEvidenceCollectionEnabled(greeting: String, name: String): String
    fun GetResolvedExternallyWorkflowSleepDurationSeconds(greeting: String, name: String): String
}
