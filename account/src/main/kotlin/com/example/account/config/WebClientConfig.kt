package com.example.account.config

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebClientConfig {
    @Bean
    fun graphQLWebClient(accountServiceProperties: AccountServiceProperties) =
        GraphQLWebClient(accountServiceProperties.auditApiUrl)
}