package br.pucpr.authserver.tasks

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<Task, Long> {
    fun findByProjectId(projectId: Long, sort: Sort): List<Task>

    fun findByProjectIdAndStatus(projectId: Long, status: TaskStatus, sort: Sort): List<Task>
}
