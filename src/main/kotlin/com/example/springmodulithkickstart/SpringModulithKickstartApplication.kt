package com.example.springmodulithkickstart

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class SpringModulithKickstartApplication

fun main(args: Array<String>) {
    runApplication<SpringModulithKickstartApplication>(*args)
}
