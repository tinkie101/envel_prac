package com.example.account.domains.account

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AccountServiceIntegrationTest(
    @Autowired private val accountService: AccountService,
    @Autowired private val accountRepository: AccountRepository
) {
    @BeforeAll
    fun setUp() {
        accountRepository.deleteAll()
    }

    @Test
    fun getAllAccounts() {
        //When
        val accounts = accountService.getAllAccounts()

        //Then
        assertThat(accounts).isNotNull
    }

    @Test
    fun getAccount() {
        //Given
        val newAccount = accountService.createNewAccount()

        //When
        val account = accountService.getAccount(newAccount.id)

        //Then
        assertThat(account.id).isEqualTo(newAccount.id)
    }

    @Test
    fun createAccount() {
        //When
        val newAccount = accountService.createNewAccount()

        //Then
        assertThat(accountService.getAllAccounts()).contains(newAccount)
    }

    @Test
    fun deleteAccount() {
        //Given
        val deleteAccount = accountService.createNewAccount()

        //When
        accountService.deleteAccount(deleteAccount.id)

        //Then
        assertThat(accountService.getAllAccounts()).doesNotContain(deleteAccount)
    }

    @Test
    fun getAccountBalance() {
        //Given
        val accountId = accountService.createNewAccount().id

        //When
        accountService.getAccountBalance(accountId)

        //Then
        assertThat(accountService.getAccountBalance(accountId)).isNotNull
    }

    @Test
    fun deposit() {
        //Given
        val accountId = accountService.createNewAccount().id
        val initialVal = accountService.getAccountBalance(accountId)
        val amount = BigDecimal.valueOf(250)

        //When
        accountService.deposit(accountId, amount)

        //Then
        assertThat(accountService.getAccountBalance(accountId)).isEqualTo(initialVal.add(amount))
    }

    @Test
    fun withdraw() {
        //Given
        val accountId = accountService.createNewAccount().id
        val initialVal = accountService.getAccountBalance(accountId)
        val amount = BigDecimal.valueOf(250)

        //When
        accountService.withdraw(accountId, amount)

        //Then
        assertThat(accountService.getAccountBalance(accountId)).isEqualTo(initialVal.subtract(amount))
    }
}