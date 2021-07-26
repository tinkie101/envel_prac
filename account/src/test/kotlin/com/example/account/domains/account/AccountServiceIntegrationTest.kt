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
    fun getBalance() {
        //Given
        val accountId = UUID.fromString("0cf2faef-3fe6-4225-a16d-40b17e714208")

        //When
        accountService.getBalance(accountId)

        //Then
        assertThat(accountService.getBalance(accountId)).isNotNull
    }

    @Test
    fun deposit() {
        //Given
        val accountId = UUID.fromString("0cf2faef-3fe6-4225-a16d-40b17e714208")
        val initialVal = accountService.getBalance(accountId)
        val amount = BigDecimal.valueOf(250)

        //When
        accountService.deposit(accountId, amount)

        //Then
        assertThat(accountService.getBalance(accountId)).isEqualTo(initialVal.add(amount))
    }

    @Test
    fun withdraw() {
        //Given
        val accountId = UUID.fromString("0cf2faef-3fe6-4225-a16d-40b17e714208")
        val initialVal = accountService.getBalance(accountId)
        val amount = BigDecimal.valueOf(250)

        //When
        accountService.withdraw(accountId, amount)

        //Then
        assertThat(accountService.getBalance(accountId)).isEqualTo(initialVal.subtract(amount))
    }
}