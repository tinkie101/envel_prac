package com.example.account.domains.account

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.stream.Stream


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccountIntegrationTest(
    @Autowired private val accountService: AccountService,
    @Autowired private val accountRepository: AccountRepository,
    @Autowired private val resourceLoader: ResourceLoader
) {
    @LocalServerPort
    private var port: Int? = 0

    @Autowired
    var webTestClient: WebTestClient? = null


    @ParameterizedTest
    @MethodSource("getQueryRequests")
    fun test_all_query_requests(query: String, method: String) {
        val testClient = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port")
            .build()

        val accountId = accountRepository.save(Account()).id!!

        val variablesString = """"variables": {
            "accountId": "$accountId",
            "amount": 250.56
        }"""

        val request = """
            {
                "operationName": "$method",
                $variablesString,
                "query": $query
            }
        """.trimIndent()

        //When
        webTestClient!!.post().uri {
            it.path("/graphql").build()
        }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .jsonPath("errors").doesNotHaveJsonPath()
            .jsonPath("error").doesNotHaveJsonPath()
    }

    companion object {
        private const val getAccountsString =
            "\"query getAccounts {\\n  accounts {\\n    id\\n    balance\\n  }\\n}\\n\\n\""

        private const val getAccountString =
            "\"query getAccount(\$accountId: UUID) {\\n  account(accountId: \$accountId) {\\n    id\\n    createdOn\\n    balance\\n  }\\n}\\n\""

        private const val createAccountString =
            "\"mutation createAccount {\\n  createAccount {\\n    id\\n  }\\n}\\n\\n\""

        private const val withdrawString =
            "\"mutation withdraw(\$accountId: UUID, \$amount: BigDecimal) {\\n  withdraw(withdrawalAccount: {accountId: \$accountId, amount: \$amount})\\n}\\n\\n\""

        private const val depositString =
            "\"mutation deposit(\$accountId: UUID, \$amount: BigDecimal) {\\n  BigDecimal: deposit(depositAccount: {accountId: \$accountId, amount: \$amount})\\n}\\n\""


        @JvmStatic
        fun getQueryRequests(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(getAccountsString, "getAccounts"),
                Arguments.of(getAccountString, "getAccount"),
                Arguments.of(createAccountString, "createAccount"),
                Arguments.of(withdrawString, "withdraw"),
                Arguments.of(depositString, "deposit"),
            )
        }
    }
}