package br.pucpr.authserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import br.pucpr.authserver.security.JwtProperties

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class AuthserverApplication

fun main(args: Array<String>) {
	runApplication<AuthserverApplication>(*args)
}
