package io.temporal.samples.cube_nde

import io.temporal.workflow.Workflow

object WorkflowVersion {
    class ToggledVersion(val changeId: String, val maxSupported: Int = 1)

    fun getBooleanValue(workflowVersion: ToggledVersion): Boolean {
        val v = Workflow.getVersion(
            workflowVersion.changeId,
            Workflow.DEFAULT_VERSION,
            workflowVersion.maxSupported,
        )
        return v != Workflow.DEFAULT_VERSION
    }
}
