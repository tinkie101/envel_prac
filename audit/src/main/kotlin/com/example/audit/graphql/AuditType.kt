package com.example.audit.graphql

import io.leangen.graphql.annotations.types.GraphQLType
import java.util.*

@GraphQLType(name = "audit")
data class AuditType(
    val accountId: UUID
)
