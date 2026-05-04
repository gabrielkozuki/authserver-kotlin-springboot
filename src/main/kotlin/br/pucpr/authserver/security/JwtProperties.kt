package br.pucpr.authserver.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val issuer: String,
    val expireHours: Long,
    val adminExpireHours: Long,
)
