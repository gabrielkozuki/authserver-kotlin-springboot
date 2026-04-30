package br.pucpr.authserver.projects.responses

import br.pucpr.authserver.projects.Project
import br.pucpr.authserver.users.responses.UserResponse
import java.time.LocalDateTime

data class ProjectResponse(
    val id: Long,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
    val members: List<UserResponse>,
) {
    constructor(project: Project) : this(
        id = project.id!!,
        name = project.name,
        description = project.description,
        createdAt = project.createdAt,
        members = project.members.map { UserResponse(it) }
    )
}
