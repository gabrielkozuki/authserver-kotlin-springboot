package br.pucpr.authserver.tasks.requests

import br.pucpr.authserver.projects.Project
import br.pucpr.authserver.tasks.Task
import br.pucpr.authserver.tasks.TaskStatus
import jakarta.validation.constraints.NotBlank

data class CreateTaskRequest(
    @NotBlank
    val title: String?,

    val description: String? = null,

    val status: TaskStatus = TaskStatus.BACKLOG,
) {
    fun toTask(project: Project) = Task(
        title = title!!,
        description = description ?: "",
        status = status,
        project = project
    )
}
