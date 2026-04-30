package br.pucpr.authserver.tasks

import br.pucpr.authserver.exceptions.ForbiddenException
import br.pucpr.authserver.lib.SortDir
import br.pucpr.authserver.projects.ProjectService
import br.pucpr.authserver.security.UserToken
import br.pucpr.authserver.tasks.requests.CreateTaskRequest
import br.pucpr.authserver.tasks.requests.UpdateTaskRequest
import br.pucpr.authserver.tasks.requests.UpdateTaskStatusRequest
import br.pucpr.authserver.tasks.responses.TaskResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projects/{projectId}/tasks")
class TaskController(
    val taskService: TaskService,
    val projectService: ProjectService,
) {
    @GetMapping
    fun list(
        @PathVariable projectId: Long,
        @RequestParam sortDir: String?,
        @RequestParam sortBy: String?,
        @RequestParam status: TaskStatus?,
    ) = taskService.findAll(
        projectId,
        SortDir.find(sortDir ?: "ASC"),
        sortBy ?: "createdAt",
        status
    )
        .map { TaskResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
    ) = taskService.findById(id, projectId)
        .let { TaskResponse(it) }
        .let { ResponseEntity.ok(it) }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun insert(
        @PathVariable projectId: Long,
        @RequestBody @Valid request: CreateTaskRequest,
        auth: Authentication,
    ): ResponseEntity<TaskResponse> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        val project = projectService.findById(projectId)
        if (!token.isAdmin && project.members.none { it.id == token.id }) throw ForbiddenException()

        return taskService.insert(request.toTask(project))
            .let { TaskResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    fun update(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
        @RequestBody @Valid request: UpdateTaskRequest,
        auth: Authentication,
    ): ResponseEntity<TaskResponse> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (!token.isAdmin && projectService.findById(projectId).members.none { it.id == token.id }) throw ForbiddenException()

        return taskService.update(
            id,
            projectId,
            request.title!!,
            request.description,
            request.status!!
        )
            .let { TaskResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{id}/status")
    fun changeStatus(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
        @RequestBody @Valid request: UpdateTaskStatusRequest,
        auth: Authentication,
    ): ResponseEntity<TaskResponse> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (!token.isAdmin && projectService.findById(projectId).members.none { it.id == token.id }) throw ForbiddenException()
        return taskService.changeStatus(id, projectId, request.status!!)
            .let { TaskResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
        auth: Authentication,
    ): ResponseEntity<Void> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (!token.isAdmin && projectService.findById(projectId).members.none { it.id == token.id }) throw ForbiddenException()

        return taskService.delete(id, projectId)
            .let {
                if (it) ResponseEntity.ok().build()
                else ResponseEntity.noContent().build()
            }
    }
}
