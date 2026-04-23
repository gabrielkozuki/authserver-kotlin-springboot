package br.pucpr.authserver.roles

import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roles")
class RoleController(val service: RoleService) {
    @GetMapping
    fun list() = service.findAll()

    @PostMapping
    @ApiResponse(responseCode = "201")
    fun insert(@RequestBody role: Role) =
        service.insert(role)
            ?.let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
            ?: ResponseEntity.badRequest().build()
}