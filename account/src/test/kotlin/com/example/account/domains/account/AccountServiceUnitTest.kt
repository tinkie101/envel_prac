package com.example.account.domains.account

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.math.BigDecimal
import java.util.*

class AccountServiceUnitTest {
    @Test
    fun getBalance() {
        // Given
        val initialBalance = BigDecimal.valueOf(250)
        val mockRepo = mock<AccountRepository> {
            val optionalAccount = Optional.of(Account(UUID.randomUUID(), initialBalance))

            on { findById(any()) }
                .doReturn(optionalAccount)
        }
        val accountService = AccountService(mockRepo)

        //When
        val balance = accountService.getBalance(UUID.randomUUID())

        //Then
        assertThat(balance).isEqualTo(initialBalance)
    }

    @Test
    fun deposit() {
        // Given
        val mockRepo = mock<AccountRepository> {
            val optionalAccount = Optional.of(Account(UUID.randomUUID(), BigDecimal.ZERO))

            on { findById(any()) }
                .doReturn(optionalAccount)
        }
        val accountService = AccountService(mockRepo)

        //When
        val balance = accountService.deposit(UUID.randomUUID(), BigDecimal.valueOf(250))

        //Then
        assertThat(balance).isEqualTo(BigDecimal.valueOf(250))
    }

    @Test
    fun withdraw() {
        // Given
        val mockRepo = mock<AccountRepository> {
            val optionalAccount = Optional.of(Account(UUID.randomUUID(), BigDecimal.valueOf(250)))

            on { findById(any()) }
                .doReturn(optionalAccount)
        }
        val accountService = AccountService(mockRepo)

        //When
        val balance = accountService.withdraw(UUID.randomUUID(), BigDecimal.valueOf(250))

        //Then
        assertThat(balance).isEqualTo(BigDecimal.ZERO)
    }
}