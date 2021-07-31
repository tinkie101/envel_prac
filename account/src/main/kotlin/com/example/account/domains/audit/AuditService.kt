package com.example.account.domains.audit

import com.example.account.graphql.client.DepositAccountMutation
import com.example.account.graphql.client.WithdrawAccountMutation
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal
import java.util.*

@Service
class AuditService {
    private val client = GraphQLWebClient("http://localhost:8081/graphql")

    fun auditDeposit() {
        runBlocking {
            val depositMutation = DepositAccountMutation(
                DepositAccountMutation.Variables(
                    UUID.randomUUID().toString(),
                    BigDecimal.ZERO.toString()
                )
            )
            val result = client.execute(depositMutation)
// TODO           {addAuthToken(token)}
            println("${result.data?.auditDeposit?.type} \$${result.data?.auditDeposit?.amount} on ${result.data?.auditDeposit?.createdOn}")
        }
    }

    fun auditWithdrawal() {
        runBlocking {
            val depositMutation = WithdrawAccountMutation(
                WithdrawAccountMutation.Variables(
                    UUID.randomUUID().toString(),
                    BigDecimal.ZERO.toString()
                )
            )
            val result = client.execute(depositMutation)
// TODO           {addAuthToken(token)}
            println("${result.data?.auditWithdrawal?.type} \$${result.data?.auditWithdrawal?.amount} on ${result.data?.auditWithdrawal?.createdOn}")
        }
    }

    // Extension function
    fun WebClient.RequestBodyUriSpec.addAuthToken(token: String) {
        header(AUTHORIZATION, "Bearer $token")
    }
}