package br.pucpr.authserver.projects

import br.pucpr.authserver.exceptions.BadRequestException
import br.pucpr.authserver.exceptions.NotFoundException
import br.pucpr.authserver.lib.SortDir
import br.pucpr.authserver.users.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProjectService(
    val repository: ProjectRepository,
    val userRepository: UserRepository,
) {
    fun findAll(sortDir: SortDir, sortBy: String, name: String?): List<Project> {
        val sort = when(sortDir) {
            SortDir.ASC -> Sort.by(sortBy).ascending()
            SortDir.DESC -> Sort.by(sortBy).descending()
        }
        return if (name != null)
            repository.findByNameContainingIgnoreCase(name, sort)
        else
            repository.findAll(sort)
    }

    fun findById(id: Long) = repository.findByIdOrNull(id) ?: throw NotFoundException("Project not found: $id")

    @Transactional
    fun insert(project: Project, creatorId: Long): Project {
        if (repository.findByName(project.name) != null) throw BadRequestException("Project name already exists!")
        val creator = userRepository.findByIdOrNull(creatorId) ?: throw NotFoundException("Creator not found!")

        project.members.add(creator)
        return repository.save(project)
            .also { log.info("Project ${it.id} created by user $creatorId.") }
    }

    @Transactional
    fun update(id: Long, name: String, description: String?): Project {
        val project = findById(id)
        if (project.name != name && repository.findByName(name) != null) throw BadRequestException("Project name already exists: $name")

        project.name = name
        if (description != null) project.description = description

        return repository.save(project)
            .also { log.info("Project ${it.id} updated.") }
    }

    @Transactional
    fun delete(id: Long): Boolean {
        val project = repository.findByIdOrNull(id) ?: return false

        repository.delete(project)
        log.info("Project $id deleted.")
        return true
    }

    @Transactional
    fun addMember(projectId: Long, userId: Long): Boolean {
        val project = findById(projectId)
        if (project.members.any { it.id == userId }) throw BadRequestException("User is already in the project!")
        val user = userRepository.findByIdOrNull(userId) ?: throw NotFoundException(userId)

        project.members.add(user)
        repository.save(project)
        log.info("User $userId added to project $projectId.")
        return true
    }

    @Transactional
    fun removeMember(projectId: Long, userId: Long): Boolean {
        val project = findById(projectId)

        project.members.removeIf { it.id == userId }
            .let {
                if (it) {
                    repository.save(project)
                    log.info("User $userId removed from project $projectId.")
                }

                return it
            }
    }

    companion object {
        val log = LoggerFactory.getLogger(ProjectService::class.java)
    }
}
