package com.example.audit.graphql

import com.example.audit.domains.transaction.TransactionDto
import io.leangen.graphql.annotations.types.GraphQLType
import java.util.*

@GraphQLType
data class AuditType(
    val accountId: UUID,
    val deposits: List<TransactionDto>,
    val withdrawals: List<TransactionDto>
)
