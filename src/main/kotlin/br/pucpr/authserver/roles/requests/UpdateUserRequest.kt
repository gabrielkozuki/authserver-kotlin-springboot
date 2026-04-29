package br.pucpr.authserver.roles.requests

import jakarta.validation.constraints.NotBlank

data class UpdateUserRequest(
    @NotBlank
    val name: String?,
)
