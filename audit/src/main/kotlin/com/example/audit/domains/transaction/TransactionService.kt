package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionTypes
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class TransactionService(private val transactionRepository: TransactionRepository) {
    fun getAccountTransactions(accountId: UUID): List<TransactionDto> =
        transactionRepository.findAll()
            .filter { it.accountId == accountId }
            .map { it.toDto() }

    fun getAccountWithdrawals(accountId: UUID): List<TransactionDto> =
        transactionRepository.findAll()
            .filter { it.accountId == accountId && it.type == TransactionTypes.WITHDRAWAL }
            .map { it.toDto() }

    fun getAccountDeposits(accountId: UUID): List<TransactionDto> =
        transactionRepository.findAll()
            .filter { it.accountId == accountId && it.type == TransactionTypes.DEPOSIT }
            .map { it.toDto() }

    fun addAccountDeposit(accountId: UUID, amount: BigDecimal) =
        Transaction(accountId = accountId, amount = amount, type = TransactionTypes.DEPOSIT)
            .let(transactionRepository::save)
            .toDto()

    fun addAccountWithdrawal(accountId: UUID, amount: BigDecimal) =
        Transaction(accountId = accountId, amount = amount, type = TransactionTypes.WITHDRAWAL)
            .let(transactionRepository::save)
            .toDto()

    // Extension function
    fun Transaction.toDto(): TransactionDto {
        return TransactionDto(id!!, accountId, amount, type, createdOn!!)
    }
}