package com.example.account.graphql

import io.leangen.graphql.annotations.types.GraphQLType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@GraphQLType(name = "account", description = "User Account with balance")
data class AccountType(
    val id: UUID,
    val balance: BigDecimal,
    val createdOn: LocalDateTime
)