package com.example.account.domains.account

import com.example.account.domains.audit.AuditService
import com.example.account.exceptions.AccountNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import javax.transaction.Transactional

@Service
class AccountService(private val accountRepository: AccountRepository, private val auditService: AuditService) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getAllAccounts(): List<AccountDto> =
        logger.debug("getAllAccounts").let {
            accountRepository.findAll().map {
                it.toDto()
            }.toList()
        }

    fun getAccount(accountId: UUID): AccountDto =
        logger.debug("getAccount $accountId").let {
            accountRepository.findById(accountId).getOrThrowIfNotFound(accountId).toDto()
        }

    fun createNewAccount(): AccountDto =
        logger.debug("createNewAccount").let {
            accountRepository.save(Account()).toDto()
        }

    fun deleteAccount(accountId: UUID) =
        logger.debug("deleteAccount $accountId").let {
            accountRepository.deleteById(accountId)
        }

    fun getAccountBalance(accountId: UUID): BigDecimal =
        logger.debug("getAccountBalance $accountId").let {
            accountRepository.findById(accountId)
                .getOrThrowIfNotFound(accountId).balance
        }

    @Transactional
    fun deposit(accountId: UUID, amount: BigDecimal): BigDecimal =
        logger.debug("deposit $accountId: \$$amount").let {
            throwIfNegative(amount).let {
                accountRepository.findById(accountId)
                    .getOrThrowIfNotFound(accountId).apply {
                        balance += amount
                    }
                    .balance
            }
        }.also { auditService.auditDeposit(accountId, amount) }

    @Transactional
    fun withdraw(accountId: UUID, amount: BigDecimal): BigDecimal =
        logger.debug("deposit $accountId: \$$amount").let {
            throwIfNegative(amount).let {
                accountRepository.findById(accountId)
                    .getOrThrowIfNotFound(accountId)
                    .apply {
                        balance -= amount
                    }.balance
            }
        }.also { auditService.auditWithdrawal(accountId, amount) }

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

    private fun Account.toDto(): AccountDto {
        return AccountDto(id!!, balance, createdOn!!)
    }
}