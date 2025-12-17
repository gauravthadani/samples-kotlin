package io.temporal.samples.springboot

import io.temporal.spring.boot.WorkerOptionsCustomizer
import io.temporal.worker.WorkerOptions
import io.temporal.worker.tuning.PollerBehaviorAutoscaling
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@SpringBootApplication
class SpringBootApp

fun main(args: Array<String>) {
    runApplication<SpringBootApp>(*args)
}


@Configuration
class TemporalWorkerConfig {
    @Bean
    fun pollerAutoscalingCustomizer(): WorkerOptionsCustomizer {
        return WorkerOptionsCustomizer { optionsBuilder: WorkerOptions.Builder, _: String, _: String ->
            optionsBuilder.apply {
                setWorkflowTaskPollersBehavior(PollerBehaviorAutoscaling(
                    1,
                    5,
                    1
                ))
                setActivityTaskPollersBehavior(PollerBehaviorAutoscaling(
                    1,
                    5,
                    1
                ))
            }
        }
    }
}