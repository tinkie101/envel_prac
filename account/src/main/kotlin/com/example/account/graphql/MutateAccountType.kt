package com.example.account.graphql

import java.math.BigDecimal
import java.util.*


data class MutateAccountType(
    val accountId: UUID,
    val amount: BigDecimal
)
