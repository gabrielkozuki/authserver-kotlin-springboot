package br.pucpr.authserver

import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class Bootstrapper (
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
): ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole = roleRepository.findByName("ADMIN")
            ?: roleRepository.save(Role(name = "ADMIN", description = "System administration"))

        roleRepository.findByName("PREMIUM")
            ?: roleRepository.save(Role(name = "PREMIUM", description = "Premium user"))

        if (userRepository.findByRole("ADMIN").isEmpty()) {
            val admin = User(
                name = "Auth Server Administrator",
                email = "admin@authserver.com",
                password = "admin",
            )
            admin.roles.add(adminRole)
            userRepository.save(admin)
        }
    }
}