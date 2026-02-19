package io.temporal.samples.hello

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.time.Duration


@WorkflowInterface
interface SignallerWorkflow {
    @WorkflowMethod
    fun greeting(wfID: String): String

}

class SignallerWorkflowImpl : SignallerWorkflow {
    override fun greeting(wfID: String): String {
//        Workflow.newExternalWorkflowStub(
//            GreetingWorkflow::class.java,
//            wfID
//        )
//            .notify(
//                "Gaurav"
//            )

        Workflow.newActivityStub(
            SignalActivities::class.java,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(30))
                .setRetryOptions(
                    RetryOptions.newBuilder().setMaximumAttempts(1).build()
                )
                .build()
        ).signal("Gaurav", wfID)

        return ""

    }
}

open class SignalActivitiesImpl : SignalActivities {
//    override fun signal(name: String, wf: String): String =
//        localClient().newUntypedWorkflowStub(wf)
//            .signal("notify", "Gaurav")
//            .let { return "" }

    override fun signal(name: String, wf: String): String {

        Workflow.newExternalWorkflowStub(
            GreetingWorkflow::class.java,
            wf
        )
            .notify(
                "Gaurav"
            )
        return ""
    }

}

@ActivityInterface
interface SignalActivities {
    @ActivityMethod
    fun signal(name: String, wf: String): String
}




