package com.example.audit.config

import com.example.audit.domains.transaction.TransactionService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.math.BigDecimal
import java.util.*

@Configuration
class CommandLineRunners(private val transactionService: TransactionService) {
    @Bean
    @Profile("populate")
    fun populateDb(): CommandLineRunner =
//        EXPLAIN ANALYZE SELECT * FROM audit.transaction WHERE account_id = 'd8504e9b-0248-4564-9c52-bc3a618736a8';
//        DROP INDEX audit.account_id_idx;
        CommandLineRunner {
            println("DB inserts started")
            val uuid = "d8504e9b-0248-4564-9c52-bc3a618736a8"
            transactionService.addAccountDeposit(UUID.fromString(uuid), BigDecimal(Math.random() * 100))
            transactionService.addAccountDeposit(UUID.fromString(uuid), BigDecimal(Math.random() * 100))

            repeat(100) {
                val accountId = UUID.randomUUID()
                repeat((Math.random() * 50).toInt()) {
                    transactionService.addAccountDeposit(accountId, BigDecimal(Math.random() * 100))
                }

                repeat((Math.random() * 50).toInt()) {
                    transactionService.addAccountWithdrawal(accountId, BigDecimal(Math.random() * 100))
                }
            }
            println("DB inserts completed")
        }
}