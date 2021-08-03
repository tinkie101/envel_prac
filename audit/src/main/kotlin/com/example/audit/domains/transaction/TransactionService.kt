package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionTypes
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class TransactionService(private val transactionRepository: TransactionRepository) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAccountTransactions(accountId: UUID): List<TransactionDto> =
        logger.debug("getAccountTransactions $accountId")
            .let {
                transactionRepository.findAllByAccountId(accountId)
                    .map { it.toDto() }
            }

    fun getAccountWithdrawals(accountId: UUID): List<TransactionDto> =
        logger.debug("getAccountWithdrawals $accountId").let {
            transactionRepository.findAllByAccountIdAndType(accountId, TransactionTypes.WITHDRAWAL)
                .map { it.toDto() }
        }

    fun getAccountDeposits(accountId: UUID): List<TransactionDto> =
        logger.debug("getAccountDeposits $accountId").let {
            transactionRepository.findAllByAccountIdAndType(accountId, TransactionTypes.DEPOSIT)
                .map { it.toDto() }
        }

    fun addAccountDeposit(accountId: UUID, amount: BigDecimal): TransactionDto =
        logger.debug("addAccountDeposit $accountId: \$$amount").let {
            SecurityContextHolder.getContext().authentication
                .let { it as JwtAuthenticationToken }
                .let { UUID.fromString(it.name) }
                .let {
                    Transaction(
                        accountId = accountId,
                        amount = amount,
                        type = TransactionTypes.DEPOSIT,
                        userId = it
                    )
                }
                .let(transactionRepository::save)
                .toDto()
        }

    fun addAccountWithdrawal(accountId: UUID, amount: BigDecimal): TransactionDto =
        logger.debug("addAccountWithdrawal $accountId: \$$amount").let {
            SecurityContextHolder.getContext().authentication
                .let { it as JwtAuthenticationToken }
                .let { UUID.fromString(it.name) }
                .let {
                    Transaction(
                        accountId = accountId,
                        amount = amount,
                        type = TransactionTypes.WITHDRAWAL,
                        userId = it
                    )
                }
                .let(transactionRepository::save)
                .toDto()
        }

    // Extension function
    fun Transaction.toDto(): TransactionDto {
        return TransactionDto(id!!, accountId, amount, type, createdOn!!, userId)
    }
}