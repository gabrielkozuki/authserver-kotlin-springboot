package br.pucpr.authserver.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    @Query("""
        SELECT DISTINCT u FROM User u
            JOIN u.roles r
            WHERE r.name = :roleName
            ORDER BY u.name
    """)
    fun findByRole(roleName: String): List<User>
}