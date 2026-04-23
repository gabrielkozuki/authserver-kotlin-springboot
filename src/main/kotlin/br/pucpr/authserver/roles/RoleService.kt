package br.pucpr.authserver.roles

import org.springframework.stereotype.Service

@Service
class RoleService(
    val repository: RoleRepository

) {
    fun findAll() = repository.findAll()

    fun insert(role: Role): Role? {
        if (repository.findByName(role.name) != null) {
            return null
        }
        return repository.save(role)
    }
}