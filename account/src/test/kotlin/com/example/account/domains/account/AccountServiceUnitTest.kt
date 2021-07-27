package com.example.account.domains.account

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.util.*

class AccountServiceUnitTest {
    @Test
    fun getAllAccounts() {
        //Given
        val mockRepo = mock<AccountRepository> {
            on { findAll() }.doReturn(listOf(Account(UUID.randomUUID()), Account(UUID.randomUUID())))
        }
        val accountService = AccountService(mockRepo)

        //When
        val accounts = accountService.getAllAccounts()

        //Then
        assertThat(accounts.size).isEqualTo(2)
    }

    @Test
    fun getAccount() {
        //Given
        val randomUUID = UUID.randomUUID()
        val mockRepo = mock<AccountRepository> {
            on { findById(any()) }.doReturn(Optional.of(Account(randomUUID)))
        }
        val accountService = AccountService(mockRepo)

        //When
        val account = accountService.getAccount(randomUUID)

        //Then
        assertThat(account.id).isEqualTo(randomUUID)
    }

    @Test
    fun createAccount() {
        //Given
        val account = Account(UUID.randomUUID())
        val mockRepo = mock<AccountRepository> {
            onGeneric { save(any()) }.doReturn(account)
        }
        val accountService = AccountService(mockRepo)

        //When
        val newAccount = accountService.createNewAccount()

        //Then
        assertThat(newAccount.id).isEqualTo(account.id)
    }

    @Test
    fun deleteAccount() {
        //Given
        val mockRepo = mock<AccountRepository>{}
        val accountService = AccountService(mockRepo)

        //When
        val randomUUID = UUID.randomUUID()
        accountService.deleteAccount(randomUUID)

        //Then
        verify(mockRepo, times(1)).deleteById(randomUUID)
        verify(mockRepo, times(1)).deleteById(any())
    }

    @Test
    fun getAccountBalance() {
        // Given
        val initialBalance = BigDecimal.valueOf(250)
        val mockRepo = mock<AccountRepository> {
            val optionalAccount = Optional.of(Account(UUID.randomUUID(), initialBalance))

            on { findById(any()) }.doReturn(optionalAccount)
        }
        val accountService = AccountService(mockRepo)

        //When
        val balance = accountService.getAccountBalance(UUID.randomUUID())

        //Then
        assertThat(balance).isEqualTo(initialBalance)
    }

    @Test
    fun deposit() {
        // Given
        val mockRepo = mock<AccountRepository> {
            val optionalAccount = Optional.of(Account(UUID.randomUUID(), BigDecimal.ZERO))

            on { findById(any()) }.doReturn(optionalAccount)
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

            on { findById(any()) }.doReturn(optionalAccount)
        }
        val accountService = AccountService(mockRepo)

        //When
        val balance = accountService.withdraw(UUID.randomUUID(), BigDecimal.valueOf(250))

        //Then
        assertThat(balance).isEqualTo(BigDecimal.ZERO)
    }
}