package com.example.audit.config

import com.example.audit.domains.transaction.Transaction
import com.example.audit.domains.transaction.TransactionRepository
import com.example.audit.enums.TransactionTypes
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.math.BigDecimal
import java.util.*

@Configuration
class CommandLineRunners(
    private val transactionRepository: TransactionRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    @Profile("populate")
    fun populateDb(): CommandLineRunner =
//        EXPLAIN ANALYZE SELECT * FROM audit.transaction WHERE account_id = 'd8504e9b-0248-4564-9c52-bc3a618736a8';
//        DROP INDEX audit.account_id_idx;
        CommandLineRunner {
            logger.debug("DB inserts started")
            val transactions = mutableListOf<Transaction>()

            var userId = UUID.fromString("d8504e9b-0248-4564-9c52-bc3a618736a8")
            var accountId = UUID.fromString("d8504e9b-0248-4564-9c52-bc3a618736a8")
            var amount = Math.random() * 100

            transactions.add(
                Transaction(
                    accountId = accountId,
                    amount = BigDecimal.valueOf(amount),
                    type = TransactionTypes.DEPOSIT,
                    userId = userId
                )
            )

            transactions.add(
                Transaction(
                    accountId = accountId,
                    amount = BigDecimal.valueOf(amount),
                    type = TransactionTypes.WITHDRAWAL,
                    userId = userId
                )
            )

            repeat(1000) {
                accountId = UUID.randomUUID()
                userId = UUID.randomUUID()
                repeat((Math.random() * 50).toInt()) {
                    amount = Math.random() * 100
                    transactions.add(
                        Transaction(
                            accountId = accountId,
                            amount = BigDecimal.valueOf(amount),
                            type = TransactionTypes.DEPOSIT,
                            userId = userId
                        )
                    )
                }

                repeat((Math.random() * 50).toInt()) {
                    amount = Math.random() * 100
                    transactions.add(
                        Transaction(
                            accountId = accountId,
                            amount = BigDecimal.valueOf(amount),
                            type = TransactionTypes.WITHDRAWAL,
                            userId = userId
                        )
                    )
                }
            }

            transactionRepository.saveAll(transactions)
            logger.debug("DB inserts completed")
        }
}