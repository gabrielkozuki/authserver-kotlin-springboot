package br.pucpr.authserver.security

import br.pucpr.authserver.users.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.jackson.io.JacksonDeserializer
import io.jsonwebtoken.jackson.io.JacksonSerializer
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date

@Component
class Jwt {
    fun createToken(user: User) =
        UserToken(user).let {
            Jwts.builder().json(JacksonSerializer())
                .signWith(Keys.hmacShaKeyFor(SECRET.toByteArray()))
                .issuedAt(utcNow().toDate())
                .expiration(utcNow().plusHours(
                    if (it.isAdmin) ADMIN_EXPIRE_HOURS else EXPIRE_HOURS
                ).toDate()
            )
            .issuer(ISSUER)
            .subject(it.id.toString())
            .claim(USER_FIELD, it)
            .compact()
        }

    fun extract(req: HttpServletRequest): Authentication? {
        try {
            val header = req.getHeader(HttpHeaders.AUTHORIZATION)
            if (header == null || !header.startsWith("Bearer ")) {
                log.debug("Token not found")
                return null
            }

            val token = header.substring(7).trim()
            val claims = Jwts.parser().json(JacksonDeserializer(
                mapOf(USER_FIELD to UserToken::class.java))
            ).verifyWith(Keys.hmacShaKeyFor(SECRET.toByteArray()))
                .build()
                .parseSignedClaims(token).payload

            if (claims.issuer != ISSUER) {
                log.trace("Invalid issuer ${claims.issuer}")
                return null
            }

            return claims
                .get(USER_FIELD, UserToken::class.java)
                .toAuthentication()

        } catch (e: Throwable) {
            log.debug(e.message)
            return null
        }
    }

    companion object {
        val SECRET = "23b2cd7e6b1d0200e7d4f9449eac49495d5db2c7"
        val ADMIN_EXPIRE_HOURS = 1L
        val EXPIRE_HOURS = 48L
        val ISSUER = "AuthServer"
        val USER_FIELD = "user"
        // TODO: gerar arquivos de configurações de ambientes diferentes, e sem ser hard-coded

        val log = LoggerFactory.getLogger(Jwt::class.java)

        private fun utcNow() = ZonedDateTime.now(ZoneOffset.UTC)

        private fun ZonedDateTime.toDate(): Date = Date.from(this.toInstant())

        private fun UserToken.toAuthentication(): Authentication {
            val authorities = roles.map { SimpleGrantedAuthority("ROLE_${it}") }
            return UsernamePasswordAuthenticationToken.authenticated(this, id, authorities)
        }
    }
}