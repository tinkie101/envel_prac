package com.example.account.domains.account

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface AccountRepository : CrudRepository<Account, UUID> {
    @Query(
        value = "SELECT * FROM account.account WHERE balance < 50.0",
        nativeQuery = true
    )
    fun findAllBalanceLessThanThan50(): List<Account>
}