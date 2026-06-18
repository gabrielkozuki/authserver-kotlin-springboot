package br.pucpr.authserver.users.responses

import br.pucpr.authserver.users.User

data class UserResponse (
    val id: Long,
    val name: String,
    val email: String,
    val avatar: String,
) {
    constructor(user: User): this(user.id!!, user.name, user.email, user.avatar)
}
