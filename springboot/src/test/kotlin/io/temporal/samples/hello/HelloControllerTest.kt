package io.temporal.samples.hello

import io.temporal.samples.springboot.MyApplicationRunner
import io.temporal.samples.springboot.main
import org.junit.jupiter.api.Test
import org.springframework.boot.DefaultApplicationArguments

class HelloControllerTest {

    @Test
    fun `test hello`() {
        MyApplicationRunner().run(DefaultApplicationArguments())
        println(":hello-world:")
    }
}
