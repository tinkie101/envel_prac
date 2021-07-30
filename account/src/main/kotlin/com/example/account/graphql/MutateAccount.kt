package com.example.account.graphql

import java.math.BigDecimal
import java.util.*


data class MutateAccount(
    val accountId: UUID,
    val amount: BigDecimal
)
