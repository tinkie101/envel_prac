package com.example.account.domains.account

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.util.*

@SpringBootTest
internal class AccountServiceIntegrationTest(@Autowired private val accountService: AccountService) {
    @Test
    fun getAllAccounts() {
        //When
        val accounts = accountService.getAllAccounts()

        //Then
        assertThat(accounts).isNotNull
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
        val accountId = UUID.fromString("0cf2faef-3fe6-4225-a16d-40b17e714208")

        //When
        accountService.getAccountBalance(accountId)

        //Then
        assertThat(accountService.getAccountBalance(accountId)).isNotNull
    }

    @Test
    fun deposit() {
        //Given
        val accountId = UUID.fromString("0cf2faef-3fe6-4225-a16d-40b17e714208")
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
        val accountId = UUID.fromString("0cf2faef-3fe6-4225-a16d-40b17e714208")
        val initialVal = accountService.getAccountBalance(accountId)
        val amount = BigDecimal.valueOf(250)

        //When
        accountService.withdraw(accountId, amount)

        //Then
        assertThat(accountService.getAccountBalance(accountId)).isEqualTo(initialVal.subtract(amount))
    }
}