package com.example.account.domains.audit

import com.example.account.graphql.client.DepositAccountMutation
import com.example.account.graphql.client.WithdrawAccountMutation
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal
import java.util.*

@Service
class AuditService(private val client: GraphQLWebClient) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun auditDeposit(accountId: UUID, amount: BigDecimal) =
        logger.debug("auditDeposit $accountId: \$$amount").let {
            runBlocking {
                val depositMutation = DepositAccountMutation(
                    DepositAccountMutation.Variables(
                        accountId.toString(),
                        amount.toString()
                    )
                )
                client.execute(depositMutation) { addAuthToken() }
            }.let { it.data?.auditDeposit }
        }

    fun auditWithdrawal(accountId: UUID, amount: BigDecimal) =
        logger.debug("auditWithdrawal $accountId: \$$amount").let {
            runBlocking {
                val withdrawalMutation = WithdrawAccountMutation(
                    WithdrawAccountMutation.Variables(
                        accountId.toString(),
                        amount.toString()
                    )
                )
                client.execute(withdrawalMutation) { addAuthToken() }
            }.let { it.data?.auditWithdrawal }
        }

    // Extension function
    fun WebClient.RequestBodyUriSpec.addAuthToken() {
        val authentication: JwtAuthenticationToken =
            SecurityContextHolder.getContext().authentication as JwtAuthenticationToken

        header(AUTHORIZATION, "Bearer ${authentication.token.tokenValue}")
    }
}