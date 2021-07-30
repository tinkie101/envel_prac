package com.example.audit.graphql

import com.example.audit.enums.TransactionTypes
import io.leangen.graphql.annotations.types.GraphQLType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@GraphQLType(name = "transaction", description = "A transaction that occurred on an account")
data class TransactionType(
    val id: UUID,
    val accountId: UUID,
    val amount: BigDecimal,
    val type: TransactionTypes,
    val createdOn: LocalDateTime
)
