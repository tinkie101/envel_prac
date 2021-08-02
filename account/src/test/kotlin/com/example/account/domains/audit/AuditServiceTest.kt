package com.example.account.domains.audit

import com.example.account.graphql.client.DepositAccountMutation
import com.example.account.graphql.client.WithdrawAccountMutation
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.web.reactive.function.client.WebClient
import java.math.BigDecimal
import java.util.*

internal class AuditServiceTest {
    @Test
    fun test_audit_deposit() {
        //Given
        val accountId = UUID.randomUUID()
        val amount = BigDecimal.ZERO
        val captor =
            argumentCaptor<GraphQLClientRequest<DepositAccountMutation>, WebClient.RequestBodyUriSpec.() -> Unit>()

        val auditService = mock<GraphQLWebClient> {
            onBlocking {
                execute(captor.first.capture(), captor.second.capture())
            }.doReturn(mock {})
        }.let(::AuditService)

        //When
        auditService.auditDeposit(accountId, amount)

        //Then
        runBlocking {
            val variables = captor.first.firstValue.variables as DepositAccountMutation.Variables
            assertThat(variables.accountId).isEqualTo(accountId.toString())
            assertThat(variables.amount).isEqualTo(amount.toString())
        }
    }

    @Test
    fun test_audit_withdrawal() {
        //Given
        val accountId = UUID.randomUUID()
        val amount = BigDecimal.ZERO
        val captor =
            argumentCaptor<GraphQLClientRequest<WithdrawAccountMutation>, WebClient.RequestBodyUriSpec.() -> Unit>()

        val auditService = mock<GraphQLWebClient> {
            onBlocking {
                execute(captor.first.capture(), captor.second.capture())
            }.doReturn(mock {})
        }.let(::AuditService)

        //When
        auditService.auditWithdrawal(accountId, amount)

        //Then
        runBlocking {
            val variables = captor.first.firstValue.variables as WithdrawAccountMutation.Variables
            assertThat(variables.accountId).isEqualTo(accountId.toString())
            assertThat(variables.amount).isEqualTo(amount.toString())
        }
    }
}