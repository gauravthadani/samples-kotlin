package io.temporal.samples.springboot

import io.temporal.api.enums.v1.WorkflowIdConflictPolicy
import io.temporal.api.enums.v1.WorkflowIdReusePolicy
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import io.temporal.samples.springboot.hello.GreetingWorkflow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class HelloController {

    @Autowired
    lateinit var client: WorkflowClient


    @GetMapping("/hello")
    fun hello(): Map<String, String> {
        return mapOf("message" to "Hello World!")
    }

    @GetMapping("/health")
    fun health(): Map<String, String> {
        return mapOf("status" to "UP")
    }

    @PostMapping(
        value = ["/hello"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.TEXT_HTML_VALUE]
    )
    fun helloSample(@RequestBody person: String): ResponseEntity<String> {
        val workflow: GreetingWorkflow =
            client.newWorkflowStub<GreetingWorkflow>(
                GreetingWorkflow::class.java,
                WorkflowOptions.newBuilder()
                    .setTaskQueue("HelloSampleTaskQueue")
                    .setWorkflowId("HelloSample")
                    .build()
            )

        return ResponseEntity.status(HttpStatus.OK).body(workflow.greeting(person))
    }
}
