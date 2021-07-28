package com.example.account.graphql

import com.example.account.domains.account.AccountController
import com.example.account.domains.account.AccountDto
import com.example.account.domains.account.AccountService
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
@GraphQLApi
class GraphQLAccountController(private val accountService: AccountService) : AccountController<AccountType> {
    @GraphQLQuery(name = "accounts", description = "Get All Accounts")
    override fun getAllAccounts(): List<AccountType> = accountService.getAllAccounts().map { it.toAccountType() }

    @GraphQLQuery(name = "account", description = "Get Account by ID")
    override fun getAccountById(accountId: UUID): AccountType = accountService.getAccount(accountId).toAccountType()

    @GraphQLMutation(name = "createAccount", description = "Create and return a new Account with random ID")
    override fun createAccount(): AccountType = accountService.createNewAccount().toAccountType()

    @GraphQLMutation(name = "withdraw", description = "Withdraw from account")
    override fun withdrawFromAccount(accountId: UUID, amount: BigDecimal): BigDecimal =
        accountService.withdraw(accountId, amount)

    @GraphQLMutation(name = "deposit", description = "Deposit into account")
    override fun depositIntoAccount(accountId: UUID, amount: BigDecimal): BigDecimal =
        accountService.deposit(accountId, amount)

    // Extension function
    private fun AccountDto.toAccountType(): AccountType = AccountType(id, balance)
}