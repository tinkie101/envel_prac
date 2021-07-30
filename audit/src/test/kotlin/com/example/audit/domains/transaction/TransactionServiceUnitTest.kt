package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionTypes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


internal class TransactionServiceUnitTest {
    private lateinit var account1: UUID
    private lateinit var account2: UUID
    private lateinit var transactions: List<Transaction>

    @BeforeEach
    fun createMockTransactions() {
        account1 = UUID.randomUUID()
        account2 = UUID.randomUUID()

        transactions = listOf(
            Transaction(
                accountId = account1,
                amount = BigDecimal.valueOf(250.5),
                type = TransactionTypes.WITHDRAWAL
            ),
            Transaction(
                accountId = account1,
                amount = BigDecimal.valueOf(20.5),
                type = TransactionTypes.DEPOSIT
            ),
            Transaction(
                accountId = account2,
                amount = BigDecimal.valueOf(895.5),
                type = TransactionTypes.WITHDRAWAL
            ),
            Transaction(
                accountId = account2,
                amount = BigDecimal.valueOf(210.0),
                type = TransactionTypes.DEPOSIT
            )
        )
    }

    @Test
    fun getAccountTransactions() {
        // Given
        val transactionService = mock<TransactionRepository> {
            on(it.findAllById(any()))
                .doReturn(transactions)
        }.let(::TransactionService)

        // When
        val transactions = transactionService.getAccountTransactions(account1)

        // Then
        assertThat(transactions).allMatch { it.accountId == account1 }
    }

    @Test
    fun addAccountDeposit() {
        // Given
        val amount = BigDecimal(180.5)
        val captor = ArgumentCaptor.forClass(Transaction::class.java)
        val mockTransaction = Transaction(
            UUID.randomUUID(),
            account1,
            amount,
            TransactionTypes.DEPOSIT,
            LocalDateTime.now()
        )
        val mockRepo = mock<TransactionRepository> {
            on(it.save(captor.capture())).doReturn(mockTransaction)
        }
        val transactionService = TransactionService(mockRepo)

        // When
        val transaction = transactionService.addAccountDeposit(account1, amount)

        // Then
        assertThat(captor.value.type).isEqualTo(TransactionTypes.DEPOSIT)
        assertThat(captor.value.amount).isEqualTo(amount)

        assertThat(transaction.accountId).isEqualTo(account1)
        assertThat(transaction.amount).isEqualTo(amount)
        assertThat(transaction.type).isEqualTo(TransactionTypes.DEPOSIT)
    }

    @Test
    fun addAccountWithdrawal() {
        // Given
        val amount = BigDecimal(180.5)
        val captor = ArgumentCaptor.forClass(Transaction::class.java)
        val mockTransaction = Transaction(
            UUID.randomUUID(),
            account1,
            amount,
            TransactionTypes.WITHDRAWAL,
            LocalDateTime.now()
        )
        val mockRepo = mock<TransactionRepository> {
            on(it.save(captor.capture())).doReturn(mockTransaction)
        }
        val transactionService = TransactionService(mockRepo)

        // When
        val transaction = transactionService.addAccountWithdrawal(account1, amount)

        // Then
        assertThat(captor.value.type).isEqualTo(TransactionTypes.WITHDRAWAL)
        assertThat(captor.value.amount).isEqualTo(amount)

        assertThat(transaction.accountId).isEqualTo(account1)
        assertThat(transaction.amount).isEqualTo(amount)
        assertThat(transaction.type).isEqualTo(TransactionTypes.WITHDRAWAL)
    }
}