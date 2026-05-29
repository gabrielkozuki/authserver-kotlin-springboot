package br.pucpr.authserver

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RootController {
    @GetMapping("/health")
    fun healthCheck() = mapOf("status" to "OK")
}