package io.temporal.samples.cube_nde

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.common.SearchAttributeKey
import io.temporal.workflow.Async
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

    private fun getActivities() = Workflow.newActivityStub(
        MyActivityInterface::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(45))
            .setRetryOptions(
                RetryOptions.newBuilder().build()
            )
            .build()
    )

    private fun untypedStub() = Workflow.newUntypedActivityStub(
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(45))
            .setRetryOptions(
                RetryOptions.newBuilder().build()
            )
            .build()
    )

    val WORKFLOW_VERSIONS = SearchAttributeKey.forKeywordList("WorkflowVersions")
    val MOST_RECENT_STARTED_ACTIVITY = SearchAttributeKey.forKeywordList("MostRecentStartedActivity")

    val activities = getActivities()

    override fun greeting(name: String): String {
        logger.info("Workflow started")
        "interrupt-curing-on-ato-transfer".versionMethod(1)
        "flatten-post-submission".versionMethod(1)
        "most-recent-started-activity-search-attribute-added".versionMethod(1)
        wrapActivityWithSA(activities::ShouldMarkAsRegZ, name)
        wrapActivityWithSA(activities::SecureAccessDevice, name)
        wrapActivityWithSA(activities::ShouldWaitForLoanPaused, name)
        wrapActivityWithSA(activities::ShouldRetrieveClaimComponents, name)
        wrapActivityWithSA(activities::GetClaimComponents, name)
        wrapActivityWithSA(activities::SaveRecordsOnInvestigationStart, name)
        wrapActivityWithSA(activities::PublishEventsOnInvestigationStart, name)
        wrapActivityWithSA(activities::StartRegulatoryWorkflows, name)
        Async.procedure {
            wrapUntypeActivityWithSA("IsAdditionalEvidenceCollectionEnabled", name)
            "flatten-ato-transfer".versionMethod(1)
            "flatten-resolved-externally".versionMethod(1)
            wrapActivityWithSA(activities::GetResolvedExternallyWorkflowSleepDurationSeconds, name)
        }
        Workflow.await { false }
        return "hello world"
    }

    private fun wrapUntypeActivityWithSA(fName: String, name: String) {
        Workflow.upsertTypedSearchAttributes(MOST_RECENT_STARTED_ACTIVITY.valueSet(listOf(fName)))
        untypedStub().executeAsync(fName, String::class.java, "hello", name)
    }

    private fun wrapActivityWithSA(f: (String, String) -> String, name: String) {
        Workflow.upsertTypedSearchAttributes(MOST_RECENT_STARTED_ACTIVITY.valueSet(listOf(f.toString())))
        f("hello", name)
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



