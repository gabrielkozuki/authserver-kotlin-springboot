package br.pucpr.authserver.tasks.responses

import br.pucpr.authserver.tasks.Task
import br.pucpr.authserver.tasks.TaskStatus
import java.time.LocalDateTime

data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val createdAt: LocalDateTime,
    val projectId: Long,
) {
    constructor(task: Task) : this(
        id = task.id!!,
        title = task.title,
        description = task.description,
        status = task.status,
        createdAt = task.createdAt,
        projectId = task.project.id!!
    )
}
