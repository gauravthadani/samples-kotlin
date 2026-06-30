package io.temporal.samples.cube_nde

import io.temporal.common.SearchAttributeKey
import io.temporal.common.SearchAttributeUpdate
import io.temporal.common.interceptors.WorkerInterceptorBase
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptor
import io.temporal.common.interceptors.WorkflowInboundCallsInterceptorBase
import io.temporal.common.interceptors.WorkflowOutboundCallsInterceptor
import io.temporal.common.interceptors.WorkflowOutboundCallsInterceptorBase
import io.temporal.workflow.Workflow

class VersionSearchAttributeInterceptor : WorkerInterceptorBase() {
    override fun interceptWorkflow(next: WorkflowInboundCallsInterceptor): WorkflowInboundCallsInterceptor =
        InboundCalls(next)

    private class InboundCalls(next: WorkflowInboundCallsInterceptor) :
        WorkflowInboundCallsInterceptorBase(next) {
        override fun init(outboundCalls: WorkflowOutboundCallsInterceptor) {
            super.init(OutboundWorkflowInterceptor(outboundCalls))
        }
    }
}

class OutboundWorkflowInterceptor(next: WorkflowOutboundCallsInterceptor?) :
    WorkflowOutboundCallsInterceptorBase(next) {

    private var next: WorkflowOutboundCallsInterceptor? = null

    init {
        this.next = next
    }

    override fun <R> executeActivity(
        input: WorkflowOutboundCallsInterceptor.ActivityInput<R>?
    ): WorkflowOutboundCallsInterceptor.ActivityOutput<R> {
        val mostRecentStartedActivitySearchAttributeAddedToggle =
            WorkflowVersion.getBooleanValue(workflowVersion = mostRecentStartedActivitySearchAttributeAddedToggle)

        if (mostRecentStartedActivitySearchAttributeAddedToggle) {
            val activityName = input?.activityName ?: "UnknownActivity"
            Workflow.upsertTypedSearchAttributes(
                SearchAttributeUpdate.valueSet(MOST_RECENT_STARTED_ACTIVITY_SEARCH_ATTRIBUTE, activityName)
            )
        }

        return next!!.executeActivity(input)
    }

    companion object {
        private val mostRecentStartedActivitySearchAttributeAddedToggle =
            WorkflowVersion.ToggledVersion(changeId = "most-recent-started-activity-search-attribute-added")

        private val MOST_RECENT_STARTED_ACTIVITY_SEARCH_ATTRIBUTE =
            SearchAttributeKey.forKeyword("MostRecentStartedActivity")
    }
}
