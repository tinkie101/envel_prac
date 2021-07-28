package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionType
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class TransactionService(private val transactionRepository: TransactionRepository) {
    fun getAccountTransactions(accountId: UUID): List<TransactionDto> =
        transactionRepository.findAll()
            .filter { it.accountId == accountId }
            .map { it.toDto() }

    fun addAccountDeposit(accountId: UUID, amount: BigDecimal) =
        Transaction(accountId = accountId, amount = amount, type = TransactionType.DEPOSIT)
            .apply(transactionRepository::save)

    fun addAccountWithdrawal(accountId: UUID, amount: BigDecimal) =
        Transaction(accountId = accountId, amount = amount, type = TransactionType.WITHDRAWAL)
            .apply(transactionRepository::save)

    // Extension function
    fun Transaction.toDto() = TransactionDto(id!!, accountId, amount, type)
}