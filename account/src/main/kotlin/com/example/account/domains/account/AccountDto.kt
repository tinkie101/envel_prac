package com.example.account.domains.account

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class AccountDto(
    val id: UUID,
    val balance: BigDecimal,
    val createdOn: LocalDateTime
)