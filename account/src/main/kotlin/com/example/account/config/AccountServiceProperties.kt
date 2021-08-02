package com.example.account.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "com.example.account")
class AccountServiceProperties {
    lateinit var auditApiUrl: String
}
