package com.example.account.graphql

import com.example.account.domains.account.AccountDto
import com.example.account.domains.account.AccountService
import io.leangen.graphql.annotations.GraphQLInputField
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
@GraphQLApi
class GraphQLAccountController(private val accountService: AccountService) {
    @GraphQLQuery(name = "accounts", description = "Get All Accounts")
    fun getAllAccounts(): List<AccountType> = accountService.getAllAccounts().map { it.toAccountType() }

    @GraphQLQuery(name = "account", description = "Get Account by ID")
    fun getAccountById(accountId: UUID): AccountType = accountService.getAccount(accountId).toAccountType()

    @GraphQLMutation(name = "createAccount", description = "Create and return a new Account with random ID")
    @PreAuthorize("hasRole('mutate')")
    fun createAccount(): AccountType = accountService.createNewAccount().toAccountType()

    @GraphQLMutation(name = "withdraw", description = "Withdraw from account")
    @PreAuthorize("hasRole('mutate')")
    fun withdrawFromAccount(@GraphQLInputField withdrawalAccount: MutateAccountType): BigDecimal =
        accountService.withdraw(withdrawalAccount.accountId, withdrawalAccount.amount)

    @GraphQLMutation(name = "deposit", description = "Deposit into account")
    @PreAuthorize("hasRole('mutate')")
    fun depositIntoAccount(@GraphQLInputField depositAccount: MutateAccountType): BigDecimal =
        accountService.deposit(depositAccount.accountId, depositAccount.amount)

    // Extension function
    private fun AccountDto.toAccountType(): AccountType = AccountType(id, balance, createdOn)
}