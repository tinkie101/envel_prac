package com.example.account.graphql

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
class AccountController(private val accountService: AccountService) {
    @GraphQLQuery
    fun accounts(): List<AccountDto> = accountService.getAllAccounts()

    @GraphQLQuery
    fun account(accountId: UUID): AccountDto = accountService.getAccount(accountId)

    @GraphQLMutation(description = "Create and return a new Account with random ID")
    fun createAccount(): AccountDto = accountService.createNewAccount()

    @GraphQLMutation
    fun withdraw(accountId: UUID, amount: BigDecimal): BigDecimal = accountService.withdraw(accountId, amount)

    @GraphQLMutation
    fun deposit(accountId: UUID, amount: BigDecimal): BigDecimal = accountService.deposit(accountId, amount)
}