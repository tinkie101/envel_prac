package com.example.audit.graphql

import com.example.audit.domains.transaction.Transaction
import com.example.audit.domains.transaction.TransactionService
import com.example.audit.enums.TransactionType
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
@GraphQLApi
class GraphQLAuditController(private val transactionService: TransactionService) {
    @GraphQLQuery(name = "audits", description = "Return an audit report for account")
    fun getAudit(accountId: UUID): AuditType = transactionService.getAccountTransactions(accountId)
        .let { transactions ->
            AuditType(
                accountId,
                transactions.filter { it.type == TransactionType.DEPOSIT },
                transactions.filter { it.type == TransactionType.WITHDRAWAL }
            )
        }

    @GraphQLMutation(name = "auditWithdrawal", description = "Add a withdrawal audit report for account")
    fun addWithdrawal(accountId: UUID, amount: BigDecimal): Transaction =
        transactionService.addAccountWithdrawal(accountId, amount)

    @GraphQLMutation(name = "auditDeposit", description = "Add a deposit audit report for account")
    fun addDeposit(accountId: UUID, amount: BigDecimal): Transaction =
        transactionService.addAccountDeposit(accountId, amount)
}