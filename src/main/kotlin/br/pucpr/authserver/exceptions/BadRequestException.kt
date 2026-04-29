package br.pucpr.authserver.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class BadRequestException(
    message: String = "Bad Request",
    cause: Throwable? = null,
): IllegalArgumentException(message, cause)