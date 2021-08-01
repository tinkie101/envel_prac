package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionTypes
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class TransactionService(private val transactionRepository: TransactionRepository) {
    fun getAccountTransactions(accountId: UUID): List<TransactionDto> =
        transactionRepository.findAllByAccountId(accountId)
            .map { it.toDto() }

    fun getAccountWithdrawals(accountId: UUID): List<TransactionDto> =
        transactionRepository.findAllByAccountId(accountId)
            .filter { it.type == TransactionTypes.WITHDRAWAL }
            .map { it.toDto() }

    fun getAccountDeposits(accountId: UUID): List<TransactionDto> =
        transactionRepository.findAllByAccountId(accountId)
            .filter { it.type == TransactionTypes.DEPOSIT }
            .map { it.toDto() }

    fun addAccountDeposit(accountId: UUID, amount: BigDecimal) =
        SecurityContextHolder.getContext().authentication
            .let { it as JwtAuthenticationToken }
            .let { UUID.fromString(it.name) }
            .let { Transaction(accountId = accountId, amount = amount, type = TransactionTypes.DEPOSIT, userId = it) }
            .let(transactionRepository::save)
            .toDto()

    fun addAccountWithdrawal(accountId: UUID, amount: BigDecimal) =
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

    // Extension function
    fun Transaction.toDto(): TransactionDto {
        return TransactionDto(id!!, accountId, amount, type, createdOn!!, userId)
    }
}