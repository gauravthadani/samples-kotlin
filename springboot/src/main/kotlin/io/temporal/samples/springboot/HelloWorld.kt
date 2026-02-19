package io.temporal.samples.springboot

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.DefaultApplicationArguments

fun main(args: Array<String>) {

    MyApplicationRunner().run(DefaultApplicationArguments())
    println(":hello-world:")
}