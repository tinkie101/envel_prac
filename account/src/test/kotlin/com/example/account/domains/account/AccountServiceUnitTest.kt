package com.example.account.domains.account

import com.example.account.enums.TransactionTypes
import com.example.account.exceptions.AccountNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class AccountServiceUnitTest {
    @Test
    fun should_get_all_accounts() {
        //Given
        val mockRepo = mock<AccountRepository> {
            on { findAll() }.doReturn(
                listOf(
                    Account(id = UUID.randomUUID(), createdOn = LocalDateTime.now()),
                    Account(id = UUID.randomUUID(), createdOn = LocalDateTime.now())
                )
            )
        }
        val accountService = AccountService(mockRepo)

        //When
        val accounts = accountService.getAllAccounts()

        //Then
        assertThat(accounts.size).isEqualTo(2)
    }

    @ParameterizedTest
    @ValueSource(strings = ["c96ca1b6-f2f5-11eb-9a03-0242ac130003", "cf9b6b12-f2f5-11eb-9a03-0242ac130003"])
    fun should_get_account(input: String) {
        val accountId = UUID.fromString(input)

        //Given
        val captor = ArgumentCaptor.forClass(UUID::class.java)
        val mockRepo = mock<AccountRepository> {
            on { findById(captor.capture()) }.doReturn(
                Optional.of(
                    Account(
                        id = accountId,
                        createdOn = LocalDateTime.now()
                    )
                )
            )
        }
        val accountService = AccountService(mockRepo)

        //When
        val account = accountService.getAccount(accountId)

        //Then
        assertThat(captor.value).isEqualTo(accountId)
        assertThat(account.id).isEqualTo(accountId)
    }

    @ParameterizedTest
    @ValueSource(strings = ["c96ca1b6-f2f5-11eb-9a03-0242ac130003", "cf9b6b12-f2f5-11eb-9a03-0242ac130003"])
    fun should_raise_exception_on_invalid_account(input: String) {
        val accountId = UUID.fromString(input)

        //Given
        val mockRepo = mock<AccountRepository> {
            on { findById(any()) }.doReturn(Optional.empty())
        }
        val accountService = AccountService(mockRepo)

        //Then
        assertThatExceptionOfType(AccountNotFoundException::class.java).isThrownBy {
            //When
            accountService.getAccount(accountId)
        }
    }

    @Test
    fun should_create_account() {
        //Given
        val randomUUID = UUID.randomUUID()
        val captor = ArgumentCaptor.forClass(Account::class.java)
        val account = Account(id = randomUUID, createdOn = LocalDateTime.now())
        val mockRepo = mock<AccountRepository> {
            onGeneric { save(captor.capture()) }.doReturn(account)
        }
        val accountService = AccountService(mockRepo)

        //When
        val accountDto = accountService.createNewAccount()

        //Then
        assertThat(captor.value.id).isNull()
        assertThat(captor.value.balance).isEqualTo(BigDecimal.ZERO)
        assertThat(captor.value.createdOn).isNull()

        assertThat(accountDto.id).isEqualTo(account.id)
        assertThat(accountDto.balance).isEqualTo(account.balance)
        assertThat(accountDto.createdOn).isEqualTo(account.createdOn)
    }

    @ParameterizedTest
    @ValueSource(strings = ["c96ca1b6-f2f5-11eb-9a03-0242ac130003", "cf9b6b12-f2f5-11eb-9a03-0242ac130003"])
    fun should_delete_account(input: String) {
        //Given
        val capture = ArgumentCaptor.forClass(UUID::class.java)
        val mockRepo = mock<AccountRepository> {
            // Weird `when` syntax?
            doNothing().`when`(it).deleteById(capture.capture())
        }
        val accountService = AccountService(mockRepo)

        //When
        val accountId = UUID.fromString(input)
        accountService.deleteAccount(accountId)

        //Then
        verify(mockRepo, times(1)).deleteById(accountId)
        assertThat(capture.value).isEqualTo(accountId)
    }

    @ParameterizedTest
    @ValueSource(doubles = [250.0, -250.0, 0.0, 0.5, -0.5, 255.522165489318, -255.16575616498689])
    fun should_get_account_balance(input: Double) {
        // Given
        val initialBalance = BigDecimal.valueOf(input)
        val capture = ArgumentCaptor.forClass(UUID::class.java)
        val accountId = UUID.randomUUID()
        val mockRepo = mock<AccountRepository> {
            val optionalAccount = Optional.of(Account(accountId, initialBalance, LocalDateTime.now()))

            on { findById(capture.capture()) }.doReturn(optionalAccount)
        }
        val accountService = AccountService(mockRepo)

        //When
        val balance = accountService.getAccountBalance(accountId)

        //Then
        assertThat(capture.value).isEqualTo(accountId)
        assertThat(balance).isEqualTo(initialBalance)
    }


    // TODO can't deposit negative amount
    @ParameterizedTest
    @ValueSource(doubles = [250.0, 0.0, 0.5, 255.522165489318])
    fun should_deposit_and_return(input: Double) {
        // Given
        val mockRepo = mock<AccountRepository> {
            val optionalAccount = Optional.of(Account(UUID.randomUUID(), BigDecimal.ZERO, LocalDateTime.now()))

            on { findById(any()) }.doReturn(optionalAccount)
        }
        val accountService = AccountService(mockRepo)

        //When
        val amount = BigDecimal.valueOf(input)
        val balance = accountService.deposit(UUID.randomUUID(), amount)

        //Then
        assertThat(balance).isEqualTo(amount)
    }

    @ParameterizedTest
    @EnumSource(TransactionTypes::class)
    fun should_throw_exception_on_invalid_id(input: TransactionTypes) {
        // Given
        val accountService = mock<AccountRepository> {
            on { findById(any()) }.doReturn(Optional.empty())
        }.let(::AccountService)

        //Then
        assertThatExceptionOfType(AccountNotFoundException::class.java).isThrownBy {
            when (input) {
                TransactionTypes.DEPOSIT -> accountService.deposit(UUID.randomUUID(), BigDecimal.ZERO)
                else -> accountService.withdraw(UUID.randomUUID(), BigDecimal.ZERO)
            }
        }
    }

    // TODO can't withdraw negative amount
    @ParameterizedTest
    @ValueSource(doubles = [250.0, 0.0, 0.5, 255.522165489318])
    fun should_withdraw_and_return(input: Double) {
        // Given
        val amount = BigDecimal.valueOf(input)
        val mockRepo = mock<AccountRepository> {
            val optionalAccount = Optional.of(Account(UUID.randomUUID(), amount, LocalDateTime.now()))

            on { findById(any()) }.doReturn(optionalAccount)
        }
        val accountService = AccountService(mockRepo)

        //When
        val balance = accountService.withdraw(UUID.randomUUID(), amount)

        //Then
        assertThat(balance).isEqualTo(BigDecimal.ZERO)
    }
}