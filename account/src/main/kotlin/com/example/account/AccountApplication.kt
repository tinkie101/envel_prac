package com.example.account

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class AccountApplication

fun main(args: Array<String>) {
    runApplication<AccountApplication>(*args)
}

@RestController
class TestController {
    @GetMapping("/unsecured")
    fun unsecured(): String = "unsecured"

    @GetMapping("/mutate")
    fun mutate(): String = "mutate"
}