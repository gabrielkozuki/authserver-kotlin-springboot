package br.pucpr.authserver.projects

import br.pucpr.authserver.exceptions.ForbiddenException
import br.pucpr.authserver.projects.requests.CreateProjectRequest
import br.pucpr.authserver.projects.requests.UpdateProjectRequest
import br.pucpr.authserver.projects.responses.ProjectResponse
import br.pucpr.authserver.security.UserToken
import br.pucpr.authserver.lib.SortDir
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projects")
class ProjectController(val service: ProjectService) {

    @GetMapping
    fun list(
        @RequestParam sortDir: String?,
        @RequestParam sortBy: String?,
        @RequestParam name: String?,
    ) = service.findAll(
            sortDir = SortDir.find(sortDir ?: "ASC"),
            sortBy = sortBy ?: "name",
            name = name,
        )
        .map { ProjectResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) =
        service.findById(id)
            .let { ProjectResponse(it) }
            .let { ResponseEntity.ok(it) }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun insert(
        @RequestBody @Valid request: CreateProjectRequest,
        auth: Authentication,
    ): ResponseEntity<ProjectResponse> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        return service.insert(request.toProject(), token.id)
            .let { ProjectResponse(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid request: UpdateProjectRequest,
        auth: Authentication,
    ): ResponseEntity<ProjectResponse> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (!token.isAdmin && service.findById(id).members.none { it.id == token.id }) throw ForbiddenException()

        return service.update(id, request.name!!, request.description)
            .let { ProjectResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        auth: Authentication,
    ): ResponseEntity<Void> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (!token.isAdmin && service.findById(id).members.none { it.id == token.id }) throw ForbiddenException()

        return service.delete(id)
            .let {
                if (it) ResponseEntity.ok().build()
                else ResponseEntity.noContent().build()
            }
    }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/users/{userId}")
    fun addMember(
        @PathVariable id: Long,
        @PathVariable userId: Long,
        auth: Authentication,
    ): ResponseEntity<Void> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (!token.isAdmin && service.findById(id).members.none { it.id == token.id }) throw ForbiddenException()

        return service.addMember(id, userId)
            .let {
                if (it) ResponseEntity.ok().build()
                else ResponseEntity.noContent().build()
            }
    }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/users/{userId}")
    fun removeMember(
        @PathVariable id: Long,
        @PathVariable userId: Long,
        auth: Authentication,
    ): ResponseEntity<Void> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (!token.isAdmin && service.findById(id).members.none { it.id == token.id }) throw ForbiddenException()

        return service.removeMember(id, userId)
            .let {
                if (it) ResponseEntity.ok().build()
                else ResponseEntity.noContent().build()
            }
    }
}
