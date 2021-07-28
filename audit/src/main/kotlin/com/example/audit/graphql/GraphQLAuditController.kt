package com.example.audit.graphql

import com.example.audit.domains.transaction.TransactionController
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class GraphQLAuditController : TransactionController {
    @GraphQLQuery
    fun hello(): String = "Hello World!"
}