package br.pucpr.authserver.users

import br.pucpr.authserver.exceptions.ForbiddenException
import br.pucpr.authserver.lib.SortDir
import br.pucpr.authserver.roles.requests.LoginRequest
import br.pucpr.authserver.users.requests.UpdateUserRequest
import br.pucpr.authserver.security.UserToken
import br.pucpr.authserver.users.requests.CreateUserRequest
import br.pucpr.authserver.users.responses.UserResponse
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users")
class UserController(val service: UserService) {
    @GetMapping("/ping")
    fun ping() = mapOf("status" to "ok")

    @GetMapping
    fun list(
        sortDir: String?,
        @RequestParam role: String?
    ): ResponseEntity<List<UserResponse>> {
        val users =
            if (role != null) service.findByRole(role)
            else service.findAll(SortDir.find(sortDir ?: "ASC"))
        return users
            .map { UserResponse(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) =
        service.findById(id)
            .let { UserResponse(it) }
            .let { ResponseEntity.ok(it) }

    @PostMapping
    @ApiResponse(responseCode = "201")
    fun insert(
        @RequestBody @Valid user: CreateUserRequest
    ) = service.insert(user.toUser())
        .let { UserResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("permitAll()")
    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid user: UpdateUserRequest,
        auth: Authentication
    ): ResponseEntity<UserResponse> {
        val token = auth.principal as? UserToken ?: throw ForbiddenException()
        if (token.id != id && !token.isAdmin) throw ForbiddenException("Update is now allowed")

        return service.update(id, user.name!!)
            ?.let { UserResponse(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()
    }

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long
    ) = service.delete(id)

    @SecurityRequirement(name = "jwt-auth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/roles/{roleName}")
    fun grant(
        @PathVariable id: Long,
        @PathVariable roleName: String
    ): ResponseEntity<Void> =
        service.addRole(id, roleName)
            .let {
                if (it) ResponseEntity.ok().build()
                else ResponseEntity.noContent().build()
            }

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid user: LoginRequest
    ) = service.login(user.email!!, user.password!!)
}