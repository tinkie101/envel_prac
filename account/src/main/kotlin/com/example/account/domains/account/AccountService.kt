package com.example.account.domains.account

import com.example.account.exceptions.AccountNotFoundException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import javax.transaction.Transactional

@Service
class AccountService(private val accountRepository: AccountRepository) {
    fun getBalance(accountId: UUID): BigDecimal =
        accountRepository.findById(accountId).getOrThrowIfNotFound(accountId).balance

    @Transactional
    fun deposit(accountId: UUID, amount: BigDecimal): BigDecimal =
        throwIfNegative(amount).let {
            accountRepository.findById(accountId)
                .getOrThrowIfNotFound(accountId).apply {
                    balance += amount
                }
                .balance
        }

    @Transactional
    fun withdraw(accountId: UUID, amount: BigDecimal): BigDecimal =
        throwIfNegative(amount).let {
            accountRepository.findById(accountId)
                .getOrThrowIfNotFound(accountId)
                .also {
                    it.balance -= amount
                }.balance
        }

    private fun throwIfNegative(amount: BigDecimal) =
        if (amount < BigDecimal.ZERO)
            throw RuntimeException("Amount cannot be less than zero")
        else amount

    // Extension function
    private fun Optional<Account>.getOrThrowIfNotFound(accountId: UUID): Account =
        when (this.isEmpty) {
            true -> throw AccountNotFoundException("User account $accountId not found")
            else -> this.get()
        }
}