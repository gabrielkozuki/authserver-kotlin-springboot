package br.pucpr.authserver.users.requests

import br.pucpr.authserver.users.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

data class CreateUserRequest (
    @NotBlank
    val name: String?,

    @NotBlank
    @Email
    val email: String?,

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,}\$")
    val password: String?,

    val bio: String?
) {
    fun toUser() = User(
        name = name ?: "",
        email = email ?: "",
        password = password ?: "",
        bio = bio ?: ""
    )
}