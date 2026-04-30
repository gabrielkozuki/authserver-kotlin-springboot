package br.pucpr.authserver

import br.pucpr.authserver.projects.Project
import br.pucpr.authserver.projects.ProjectRepository
import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
class Bootstrapper(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val projectRepository: ProjectRepository,
) : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole = roleRepository.findByName("ADMIN")
            ?: roleRepository.save(Role(name = "ADMIN", description = "System administration"))

        val premiumRole = roleRepository.findByName("PREMIUM")
            ?: roleRepository.save(Role(name = "PREMIUM", description = "Premium user"))

        val admin = if (userRepository.findByRole("ADMIN").isEmpty()) {
            User(name = "Auth Server Administrator", email = "admin@authserver.com", password = "admin")
                .also { it.roles.add(adminRole) }
                .let { userRepository.save(it) }
        } else {
            userRepository.findByRole("ADMIN").first()
        }

        val gabriel = userRepository.findByEmail("gabriel@authserver.com")
            ?: User(name = "Gabriel", email = "gabriel@authserver.com", password = "senha123")
                .let { userRepository.save(it) }

        val juliao = userRepository.findByEmail("juliao@authserver.com")
            ?: User(name = "Julião", email = "juliao@authserver.com", password = "senha123")
                .also { it.roles.add(premiumRole) }
                .let { userRepository.save(it) }

        if (projectRepository.count() == 0L) {
            projectRepository.save(
                Project(name = "Backend", description = "API REST com Kotlin e Spring")
                    .also { it.members.addAll(listOf(admin, gabriel)) }
            )
            projectRepository.save(
                Project(name = "Frontend", description = "Interface web do sistema")
                    .also { it.members.addAll(listOf(gabriel, juliao)) }
            )
        }
    }
}
