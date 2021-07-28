package com.example.account.domains.account

import com.example.account.graphql.AccountType
import java.math.BigDecimal
import java.util.*

interface AccountController {
    fun getAllAccounts(): List<AccountType>

    /**
     * @param accountId The ID for the account to be returned
     * @return AccountType
     */
    fun getAccountById(accountId: UUID): AccountType

    fun createAccount(): AccountType

    /**
     * @param accountId The ID for the account
     * @param amount The amount to be withdrawn
     * @return The account's new balance amount
     */
    fun withdrawFromAccount(accountId: UUID, amount: BigDecimal): BigDecimal

    /**
     * @param accountId The ID for the account
     * @param amount The amount to be deposited
     * @return The account's new balance amount
     */
    fun depositIntoAccount(accountId: UUID, amount: BigDecimal): BigDecimal
}