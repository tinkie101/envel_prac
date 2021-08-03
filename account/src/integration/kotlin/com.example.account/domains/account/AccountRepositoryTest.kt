package com.example.account.domains.account

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.math.BigDecimal

@DataJpaTest // Only loads @Repository, EntityManager, TestEntityManager contexts
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AccountRepositoryTest(@Autowired private val accountRepository: AccountRepository) {

    @BeforeEach
    fun createTestData() {
        accountRepository.deleteAll()
        //Given
        accountRepository.save(Account(balance = BigDecimal.valueOf(250)))
        accountRepository.save(Account(balance = BigDecimal.ZERO))
    }

    @AfterEach
    fun clearTestData() {
        accountRepository.deleteAll()
    }

    @Test
    fun `should get all_less_than_50`() {
        //When
        val accounts = accountRepository.findAllBalanceLessThanThan50()

        //Then
        assertThat(accounts.size).isEqualTo(1)
    }
}