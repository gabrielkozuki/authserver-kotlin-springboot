package br.pucpr.authserver.tasks.requests

import br.pucpr.authserver.tasks.TaskStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateTaskRequest(
    @NotBlank
    val title: String?,

    val description: String? = null,

    @field:NotNull
    var status: TaskStatus?,
)
