package com.example.audit.graphql

import java.math.BigDecimal
import java.util.*


data class MutateAccount(
    val accountId: UUID,
    val amount: BigDecimal
)
