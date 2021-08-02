package com.example.account.domains.account

import com.example.account.enums.TransactionTypes
import com.example.account.exceptions.AccountNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Stream

class AccountServiceUnitTest {
    @Test
    fun should_get_all_accounts() {
        //Given
        val accountService = mock<AccountRepository> {
            on { findAll() }.doReturn(
                listOf(
                    Account(id = UUID.randomUUID(), createdOn = LocalDateTime.now()),
                    Account(id = UUID.randomUUID(), createdOn = LocalDateTime.now())
                )
            )
        }.let(::AccountService)

        //When
        val accounts = accountService.getAllAccounts()

        //Then
        assertThat(accounts.size).isEqualTo(2)
    }

    @Test
    fun should_create_account() {
        //Given
        val randomUUID = UUID.randomUUID()
        val captor = ArgumentCaptor.forClass(Account::class.java)
        val account = Account(id = randomUUID, createdOn = LocalDateTime.now())
        val accountService = mock<AccountRepository> {
            onGeneric { save(captor.capture()) }.doReturn(account)
        }.let(::AccountService)

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
    fun should_get_account(input: String) {
        val accountId = UUID.fromString(input)

        //Given
        val captor = ArgumentCaptor.forClass(UUID::class.java)
        val accountService = mock<AccountRepository> {
            on { findById(captor.capture()) }.doReturn(
                Optional.of(
                    Account(
                        id = accountId,
                        createdOn = LocalDateTime.now()
                    )
                )
            )
        }.let(::AccountService)

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
        val accountService = mock<AccountRepository> {
            on { findById(any()) }.doReturn(Optional.empty())
        }.let(::AccountService)

        //Then
        assertThatExceptionOfType(AccountNotFoundException::class.java).isThrownBy {
            //When
            accountService.getAccount(accountId)
        }
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
        val accountService = mock<AccountRepository> {
            val optionalAccount = Optional.of(Account(accountId, initialBalance, LocalDateTime.now()))

            on { findById(capture.capture()) }.doReturn(optionalAccount)
        }.let(::AccountService)

        //When
        val balance = accountService.getAccountBalance(accountId)

        //Then
        assertThat(capture.value).isEqualTo(accountId)
        assertThat(balance).isEqualTo(initialBalance)
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
                TransactionTypes.WITHDRAWAL -> accountService.withdraw(UUID.randomUUID(), BigDecimal.ZERO)
                else -> fail("Unknown operation type $input")
            }
        }
    }

    @ParameterizedTest
    @EnumSource(TransactionTypes::class)
    fun should_throw_exception_on_negative_amount(input: TransactionTypes) {
        // Given
        val accountService = mock<AccountRepository> {
            on { findById(any()) }.doReturn(Optional.empty())
        }.let(::AccountService)

        //Then
        assertThatExceptionOfType(RuntimeException::class.java).isThrownBy {
            val negativeAmount = BigDecimal.valueOf(-25.0)
            when (input) {
                TransactionTypes.DEPOSIT -> accountService.deposit(UUID.randomUUID(), negativeAmount)
                TransactionTypes.WITHDRAWAL -> accountService.withdraw(UUID.randomUUID(), negativeAmount)
                else -> fail("Unknown operation type $input")
            }
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = [250.0, 0.0, 0.5, 255.522165489318])
    fun should_deposit_and_return(input: Double) {
        // Given
        val account = UUID.randomUUID()
        val accountService = mock<AccountRepository> {
            val optionalAccount = Optional.of(Account(account, BigDecimal.ZERO, LocalDateTime.now()))

            on { findById(any()) }.doReturn(optionalAccount)
        }.let(::AccountService)

        //When
        val amount = BigDecimal.valueOf(input)
        val balance = accountService.deposit(account, amount)

        //Then
        assertThat(balance).isEqualTo(amount)
    }

    @ParameterizedTest
    @MethodSource("provideWithdrawInput")
    fun test_withdraw(
        found: Boolean,
        initialBalance: BigDecimal,
        amount: BigDecimal,
        then: (() -> BigDecimal) -> Unit
    ) {
        // Given
        val account = UUID.randomUUID()
        val accountService = mock<AccountRepository> {
            val optionalAccount =
                if (found)
                    Optional.of(Account(account, initialBalance, LocalDateTime.now()))
                else
                    Optional.empty()
            on { findById(any()) }.doReturn(optionalAccount)
        }.let(::AccountService)

        //Then
        then.invoke {
            //when
            accountService.withdraw(account, amount)
        }
    }

    companion object {
        @JvmStatic
        fun provideWithdrawInput(): Stream<Arguments> {
            /**
             *    account_found, initialBalance, withdrawAmount, test
             */
            return Stream.of(
                Arguments.of(
                    true,
                    BigDecimal.valueOf(250.0),
                    BigDecimal.valueOf(250.0),
                    isEqualTo(BigDecimal.valueOf(0.0))
                ),
                Arguments.of(
                    true,
                    BigDecimal.valueOf(250.0),
                    BigDecimal.valueOf(0.0),
                    isEqualTo(BigDecimal.valueOf(250.0))
                ),
                Arguments.of(
                    true,
                    BigDecimal.valueOf(0.5),
                    BigDecimal.valueOf(0.5),
                    isEqualTo(BigDecimal.valueOf(0.0))
                ),
                Arguments.of(
                    true,
                    BigDecimal.valueOf(200.0),
                    BigDecimal.valueOf(255),
                    isEqualTo(BigDecimal.valueOf(-55.0))
                ),
                //Expect exception
                Arguments.of(
                    true,
                    BigDecimal.valueOf(200.0),
                    BigDecimal.valueOf(-255),
                    isExceptionNotInline(RuntimeException::class.java)
                ),
                Arguments.of(
                    false,
                    BigDecimal.valueOf(200.0),
                    BigDecimal.valueOf(255.0),
                    isException<AccountNotFoundException>()
                ),
            )
        }

        private fun isEqualTo(amount: Any) = { method: () -> BigDecimal ->
            val balance = method()
            assertThat(balance).isEqualTo(amount)
        }

        private inline fun <reified T : Exception> isException() = { method: () -> BigDecimal ->
            assertThatExceptionOfType(T::class.java).isThrownBy {
                method()
            }
        }

        private fun <T : Exception> isExceptionNotInline(exception: Class<T>) = { method: () -> BigDecimal ->
            assertThatExceptionOfType(exception).isThrownBy {
                method()
            }
        }
    }
}