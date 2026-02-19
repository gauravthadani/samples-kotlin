package io.temporal.samples.springboot

import io.temporal.spring.boot.WorkerOptionsCustomizer
import io.temporal.worker.WorkerOptions
import io.temporal.worker.tuning.PollerBehaviorAutoscaling
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.util.function.Consumer


@SpringBootApplication
class SpringBootApp

fun main(args: Array<String>) {
    runApplication<SpringBootApp>(*args)
}



@Component
class MyApplicationRunner : ApplicationRunner {
    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        println("ApplicationRunner executed with options:")
        println("Application runner - THREAD INFO AFTER: ${Thread.currentThread().name} - ${Thread.currentThread().contextClassLoader}")
    }
}


@Configuration
class TemporalWorkerConfig {
    @Bean
    fun pollerAutoscalingCustomizer(): WorkerOptionsCustomizer {
        println("THREAD INFO AFTER: ${Thread.currentThread().name} - ${Thread.currentThread().contextClassLoader}")
        return WorkerOptionsCustomizer { optionsBuilder: WorkerOptions.Builder, _: String, _: String ->
            optionsBuilder.apply {

                setWorkflowTaskPollersBehavior(PollerBehaviorAutoscaling(
                    1,
                    5, 1
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