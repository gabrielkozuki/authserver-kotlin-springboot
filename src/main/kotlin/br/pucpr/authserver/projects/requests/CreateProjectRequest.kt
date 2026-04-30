package br.pucpr.authserver.projects.requests

import br.pucpr.authserver.projects.Project
import jakarta.validation.constraints.NotBlank

data class CreateProjectRequest(
    @NotBlank
    val name: String?,
    val description: String? = null,
) {
    fun toProject() = Project(name = name!!, description = description ?: "")
}
