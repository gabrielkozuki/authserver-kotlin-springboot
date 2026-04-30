package br.pucpr.authserver.tasks

import br.pucpr.authserver.exceptions.NotFoundException
import br.pucpr.authserver.lib.SortDir
import br.pucpr.authserver.projects.ProjectRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TaskService(
    val repository: TaskRepository,
    val projectRepository: ProjectRepository,
) {
    fun findAll(
        projectId: Long,
        sortDir: SortDir,
        sortBy: String,
        status: TaskStatus?
    ): List<Task> {
        if (projectRepository.findByIdOrNull(projectId) == null) throw NotFoundException("Project not found: $projectId")

        val sort = when(sortDir) {
            SortDir.ASC -> Sort.by(sortBy).ascending()
            SortDir.DESC -> Sort.by(sortBy).descending()
        }
        return if (status != null)
            repository.findByProjectIdAndStatus(projectId, status, sort)
        else
            repository.findByProjectId(projectId, sort)
    }

    fun findById(id: Long, projectId: Long): Task {
        val task = repository.findByIdOrNull(id) ?: throw NotFoundException("Task not found: $id")
        if (task.project.id != projectId) throw NotFoundException("Project not found: $projectId")

        return task
    }

    @Transactional
    fun insert(task: Task): Task =
        repository.save(task)
            .also { log.info("Task ${it.id} created in project ${it.project.id}.") }

    @Transactional
    fun update(
        id: Long,
        projectId: Long,
        title: String,
        description: String?,
        status: TaskStatus
    ): Task {
        val task = findById(id, projectId)

        task.title = title
        if (description != null) task.description = description
        task.status = status

        return repository.save(task)
            .also { log.info("Task ${it.id} updated.") }
    }

    @Transactional
    fun changeStatus(
        id: Long,
        projectId: Long,
        status: TaskStatus
    ): Task {
        val task = findById(id, projectId)
        task.status = status

        return repository.save(task)
            .also { log.info("Task ${it.id} status changed to $status.") }
    }

    @Transactional
    fun delete(id: Long, projectId: Long): Boolean {
        val task = repository.findByIdOrNull(id) ?: return false
        if (task.project.id != projectId) return false

        repository.delete(task)
        log.info("Task $id deleted.")
        return true
    }

    companion object {
        val log = LoggerFactory.getLogger(TaskService::class.java)
    }
}
