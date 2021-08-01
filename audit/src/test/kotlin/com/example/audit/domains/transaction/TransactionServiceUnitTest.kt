package com.example.audit.domains.transaction

import com.example.audit.enums.TransactionTypes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


internal class TransactionServiceUnitTest {
    private lateinit var account1: UUID
    private lateinit var account2: UUID
    private lateinit var authUserId: UUID
    private lateinit var transactions: List<Transaction>

    @BeforeEach
    fun createMockTransactions() {
        //Security context
        authUserId = UUID.randomUUID()
        mock<JwtAuthenticationToken> { mockJwt ->
            on(mockJwt.name).doReturn(authUserId.toString())
        }.let {
            mock<SecurityContext> { mockSecurity ->
                on(mockSecurity.authentication)
                    .doReturn(it)
            }
        }.let {
            SecurityContextHolder.setContext(it)
        }

        // Dummy data
        account1 = UUID.randomUUID()
        account2 = UUID.randomUUID()

        transactions = listOf(
            Transaction(
                UUID.randomUUID(),
                account1,
                BigDecimal.valueOf(250.5),
                TransactionTypes.WITHDRAWAL,
                LocalDateTime.now(),
                UUID.randomUUID()
            ),
            Transaction(
                UUID.randomUUID(),
                account1,
                BigDecimal.valueOf(20.5),
                TransactionTypes.DEPOSIT,
                LocalDateTime.now(),
                UUID.randomUUID()
            ),
            Transaction(
                UUID.randomUUID(),
                account2,
                BigDecimal.valueOf(895.5),
                TransactionTypes.WITHDRAWAL,
                LocalDateTime.now(),
                UUID.randomUUID()
            ),
            Transaction(
                UUID.randomUUID(),
                account2,
                BigDecimal.valueOf(210.0),
                TransactionTypes.DEPOSIT,
                LocalDateTime.now(),
                UUID.randomUUID()
            )
        )
    }

    @Test
    fun getAccountTransactions() {
        // Given
        val transactionService = mock<TransactionRepository> { mockRepo ->
            on(mockRepo.findAllByAccountId(any()))
                .doReturn(transactions.filter { it.accountId == account1 })
        }.let(::TransactionService)

        // When
        val results = transactionService.getAccountTransactions(account1)

        // Then
        assertThat(results).allMatch { it.accountId == account1 }
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
            LocalDateTime.now(),
            authUserId
        )
        val mockRepo = mock<TransactionRepository> {
            on(it.save(captor.capture())).doReturn(mockTransaction)
        }
        val transactionService = TransactionService(mockRepo)

        // When
        val transactionDto = transactionService.addAccountDeposit(account1, amount)

        // Then
        assertThat(captor.value.type).isEqualTo(TransactionTypes.DEPOSIT)
        assertThat(captor.value.amount).isEqualTo(amount)

        assertThat(transactionDto.accountId).isEqualTo(account1)
        assertThat(transactionDto.amount).isEqualTo(amount)
        assertThat(transactionDto.type).isEqualTo(TransactionTypes.DEPOSIT)
        assertThat(transactionDto.userId).isEqualTo(authUserId)
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
            LocalDateTime.now(),
            authUserId
        )
        val mockRepo = mock<TransactionRepository> {
            on(it.save(captor.capture())).doReturn(mockTransaction)
        }
        val transactionService = TransactionService(mockRepo)

        // When
        val transactionDto = transactionService.addAccountWithdrawal(account1, amount)

        // Then
        assertThat(captor.value.type).isEqualTo(TransactionTypes.WITHDRAWAL)
        assertThat(captor.value.amount).isEqualTo(amount)

        assertThat(transactionDto.amount).isEqualTo(amount)
        assertThat(transactionDto.type).isEqualTo(TransactionTypes.WITHDRAWAL)
        assertThat(transactionDto.userId).isEqualTo(authUserId)
    }
}