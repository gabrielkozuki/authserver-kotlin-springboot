package br.pucpr.authserver.projects

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {
    fun findByName(name: String): Project?

    fun findByNameContainingIgnoreCase(name: String, sort: Sort): List<Project>
}
