package br.pucpr.authserver.users

import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
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
    fun list(sortDir: String?, @RequestParam role: String?) =
        if (role != null) {
            service.findByRole(role)
                .let { ResponseEntity.ok(it) }
        } else {
            SortDir.findOrNull(sortDir ?: "ASC")
                ?.let { service.findAll(it) }
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.badRequest().build()
        }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) =
        service.findByIdOrNull(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    @ApiResponse(responseCode = "201")
    fun insert(@RequestBody user: User) =
        service.insert(user)
            ?.let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
            ?: ResponseEntity.badRequest().build()

    @DeleteMapping("/{id}")
    fun delete(@RequestBody id: Long) =
        service.delete(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PutMapping("/{id}/roles/{roleName}")
    fun grant(@PathVariable id: Long, @PathVariable roleName: String): ResponseEntity<Void> =
        service.addRole(id, roleName.uppercase())
            ?.let { if (it) ResponseEntity.ok().build() else ResponseEntity.noContent().build() }
            ?: ResponseEntity.badRequest().build()

}