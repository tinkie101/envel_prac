package com.example.account.config

import com.nimbusds.jose.shaded.json.JSONArray
import com.nimbusds.jose.shaded.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class JWTSecurityConfig : WebSecurityConfigurerAdapter() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun configure(http: HttpSecurity) =
        http.authorizeRequests()
            .antMatchers(HttpMethod.GET, "/gui").permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2ResourceServer().jwt()
            .jwtAuthenticationConverter(getJwtAuthenticationConverter())
            .let { logger.debug("configured") }

    private fun getJwtAuthenticationConverter(): JwtAuthenticationConverter =
        JwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter {
                val roles = ((it.claims["realm_access"] as JSONObject)["roles"] as JSONArray).map {
                    GrantedAuthority { "ROLE_$it" }
                }
                val scopes = (it.claims["scope"] as String).split(" ").map {
                    GrantedAuthority { "SCOPE_$it" }
                }
                roles + scopes
            }
        }.also { logger.debug("JwtAuthenticationConverter") }
}