package com.example.account.graphql

import com.example.account.domains.account.AccountDto
import com.example.account.domains.account.AccountService
import com.example.account.domains.audit.AuditService
import io.leangen.graphql.annotations.GraphQLInputField
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
@GraphQLApi
class GraphQLAccountController(private val accountService: AccountService, private val auditService: AuditService) {
    @GraphQLQuery(name = "accounts", description = "Get All Accounts")
    fun getAllAccounts(): List<AccountType> = accountService.getAllAccounts().map { it.toAccountType() }

    @GraphQLQuery(name = "account", description = "Get Account by ID")
    fun getAccountById(accountId: UUID): AccountType = accountService.getAccount(accountId).toAccountType()

    @GraphQLMutation(name = "createAccount", description = "Create and return a new Account with random ID")
    fun createAccount(): AccountType = accountService.createNewAccount().toAccountType()

    @GraphQLMutation(name = "withdraw", description = "Withdraw from account")
    fun withdrawFromAccount(@GraphQLInputField withdrawalAccount: MutateAccount): BigDecimal =
        accountService.withdraw(withdrawalAccount.accountId, withdrawalAccount.amount)
            .also { auditService.auditWithdrawal() }

    @GraphQLMutation(name = "deposit", description = "Deposit into account")
    fun depositIntoAccount(@GraphQLInputField depositAccount: MutateAccount): BigDecimal =
        accountService.deposit(depositAccount.accountId, depositAccount.amount).also { auditService.auditDeposit() }

    // Extension function
    private fun AccountDto.toAccountType(): AccountType = AccountType(id, balance, createdOn)
}