package com.example.account.graphql

import io.leangen.graphql.annotations.types.GraphQLType
import java.math.BigDecimal
import java.util.*

@GraphQLType(description = "User Account with balance")
data class AccountType(
    val id: UUID,
    val balance: BigDecimal
)