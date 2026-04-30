package br.pucpr.authserver

import br.pucpr.authserver.projects.Project
import br.pucpr.authserver.projects.ProjectRepository
import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.tasks.Task
import br.pucpr.authserver.tasks.TaskRepository
import br.pucpr.authserver.tasks.TaskStatus
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
    val taskRepository: TaskRepository,
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
            val backend = projectRepository.save(
                Project(name = "Backend", description = "API REST com Kotlin e Spring")
                    .also { it.members.addAll(listOf(admin, gabriel)) }
            )
            val frontend = projectRepository.save(
                Project(name = "Frontend", description = "Interface web do sistema")
                    .also { it.members.addAll(listOf(gabriel, juliao)) }
            )
            taskRepository.saveAll(listOf(
                Task(title = "Configurar banco de dados", status = TaskStatus.DONE, project = backend),
                Task(title = "Implementar autenticação JWT", status = TaskStatus.DONE, project = backend),
                Task(title = "Criar módulo de projetos", status = TaskStatus.IN_PROGRESS, project = backend),
                Task(title = "Criar módulo de tasks", project = backend),
                Task(title = "Criar layout inicial", status = TaskStatus.DONE, project = frontend),
                Task(title = "Implementar tela de login", status = TaskStatus.IN_PROGRESS, project = frontend),
                Task(title = "Integrar com a API", project = frontend),
            ))
        }
    }
}
