package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.math.BigDecimal
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
                type = TransactionType.WITHDRAWAL
            ),
            Transaction(
                accountId = account1,
                amount = BigDecimal.valueOf(20.5),
                type = TransactionType.DEPOSIT
            ),
            Transaction(
                accountId = account2,
                amount = BigDecimal.valueOf(895.5),
                type = TransactionType.WITHDRAWAL
            ),
            Transaction(
                accountId = account2,
                amount = BigDecimal.valueOf(210.0),
                type = TransactionType.DEPOSIT
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
        val captor = ArgumentCaptor.forClass(Transaction::class.java)
        val mockTransaction = mock<Transaction> {}
        val mockRepo = mock<TransactionRepository> {
            on(it.save(captor.capture())).doReturn(mockTransaction)
        }
        val transactionService = TransactionService(mockRepo)

        // When
        val amount = BigDecimal(180.5)
        val transaction = transactionService.addAccountDeposit(account1, amount)

        // Then
        assertThat(captor.value.type).isEqualTo(TransactionType.DEPOSIT)
        assertThat(captor.value.amount).isEqualTo(amount)

        assertThat(transaction.accountId).isEqualTo(account1)
        assertThat(transaction.amount).isEqualTo(amount)
        assertThat(transaction.type).isEqualTo(TransactionType.DEPOSIT)
    }

    @Test
    fun addAccountWithdrawal() {
        // Given
        val captor = ArgumentCaptor.forClass(Transaction::class.java)
        val mockTransaction = mock<Transaction> {}
        val mockRepo = mock<TransactionRepository> {
            on(it.save(captor.capture())).doReturn(mockTransaction)
        }
        val transactionService = TransactionService(mockRepo)

        // When
        val amount = BigDecimal(180.5)
        val transaction = transactionService.addAccountWithdrawal(account1, amount)

        // Then
        assertThat(captor.value.type).isEqualTo(TransactionType.WITHDRAWAL)
        assertThat(captor.value.amount).isEqualTo(amount)

        assertThat(transaction.accountId).isEqualTo(account1)
        assertThat(transaction.amount).isEqualTo(amount)
        assertThat(transaction.type).isEqualTo(TransactionType.WITHDRAWAL)
    }
}