package com.example.audit.graphql

import com.example.audit.domains.transaction.TransactionDto
import com.example.audit.domains.transaction.TransactionService
import io.leangen.graphql.annotations.GraphQLContext
import io.leangen.graphql.annotations.GraphQLInputField
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component
import java.util.*

@Component
@GraphQLApi
class GraphQLAuditController(private val transactionService: TransactionService) {
    @GraphQLQuery(name = "helloWorld")
    fun helloWorld(): String = "Hello World"

    @GraphQLQuery(name = "audit", description = "Return an audit report for account")
    fun getAudit(accountId: UUID): AuditType = AuditType(accountId)

    @GraphQLQuery(name = "withdrawals", description = "Return all the withdrawals for an account")
    fun getAuditWithdrawals(@GraphQLContext auditType: AuditType): List<TransactionType> =
        transactionService.getAccountWithdrawals(auditType.accountId).map { it.toGraphQLType() }

    @GraphQLQuery(name = "deposits", description = "Return all the deposits for an account")
    fun getAuditDeposits(@GraphQLContext auditType: AuditType): List<TransactionType> =
        transactionService.getAccountDeposits(auditType.accountId).map { it.toGraphQLType() }

    @GraphQLMutation(name = "auditWithdrawal", description = "Add a withdrawal audit report for account")
    fun addWithdrawal(@GraphQLInputField withdrawAccount: MutateAccount): TransactionType =
        transactionService.addAccountWithdrawal(withdrawAccount.accountId, withdrawAccount.amount).toGraphQLType()

    @GraphQLMutation(name = "auditDeposit", description = "Add a deposit audit report for account")
    fun addDeposit(@GraphQLInputField depositAccount: MutateAccount): TransactionType =
        transactionService.addAccountDeposit(depositAccount.accountId, depositAccount.amount).toGraphQLType()

    // Extension function
    fun TransactionDto.toGraphQLType(): TransactionType {
        return TransactionType(id, accountId, amount, type, createdOn)
    }
}