package br.pucpr.authserver.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedException(
    message: String = "Unauthorized",
    cause: Throwable? = null,
): IllegalArgumentException(message, cause)