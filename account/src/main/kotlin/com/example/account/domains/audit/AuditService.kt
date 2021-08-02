package com.example.account.domains.audit

import com.example.account.graphql.client.DepositAccountMutation
import com.example.account.graphql.client.WithdrawAccountMutation
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal
import java.util.*

@Service
class AuditService(private val client: GraphQLWebClient) {

    fun auditDeposit(accountId: UUID, amount: BigDecimal) {
        runBlocking {
            val depositMutation = DepositAccountMutation(
                DepositAccountMutation.Variables(
                    accountId.toString(),
                    amount.toString()
                )
            )
            val result = client.execute(depositMutation) { addAuthToken() }.let { it?.data?.auditDeposit }
            println("${result?.type} \$${result?.amount} on ${result?.createdOn}")
        }
    }

    fun auditWithdrawal(accountId: UUID, amount: BigDecimal) {
        runBlocking {
            val depositMutation = WithdrawAccountMutation(
                WithdrawAccountMutation.Variables(
                    accountId.toString(),
                    amount.toString()
                )
            )
            val result = client.execute(depositMutation) { addAuthToken() }.let { it?.data?.auditWithdrawal }
            println("${result?.type} \$${result?.amount} on ${result?.createdOn}")
        }
    }

    // Extension function
    fun WebClient.RequestBodyUriSpec.addAuthToken() {
        val authentication: JwtAuthenticationToken =
            SecurityContextHolder.getContext().authentication as JwtAuthenticationToken

        header(AUTHORIZATION, "Bearer ${authentication.token.tokenValue}")
    }
}