package br.pucpr.authserver.users

import br.pucpr.authserver.exceptions.BadRequestException
import br.pucpr.authserver.exceptions.NotFoundException
import br.pucpr.authserver.exceptions.UnauthorizedException
import br.pucpr.authserver.integration.quotes.QuoteClient
import br.pucpr.authserver.lib.SortDir
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.security.Jwt
import br.pucpr.authserver.users.responses.LoginResponse
import br.pucpr.authserver.users.responses.UserResponse
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    val repository: UserRepository,
    val roleRepository: RoleRepository,
    val avatarService: AvatarService,
    val jwt: Jwt,
    val quoteClient: QuoteClient,
) {
    fun findAll(sortDir: SortDir) = when(sortDir) {
        SortDir.ASC -> repository.findAll(Sort.by("name").ascending())
        SortDir.DESC -> repository.findAll(Sort.by("name").descending())
    }

    fun findByRole(roleName: String) = repository.findByRole(roleName.uppercase())

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun findById(id: Long) = findByIdOrNull(id) ?: throw NotFoundException(id)

    fun insert(user: User): User {
        if (repository.findByEmail(user.email) != null) {
            throw BadRequestException("User already exists")
        }
        if (user.bio.isEmpty()) {
            user.bio = quoteClient.randomQuote()?.text ?: ""
        }

        return repository.save(user)
            .also { log.info("User ${it.id} added.") }
    }

    fun update(id: Long, name: String): User? {
        val user = repository.findByIdOrNull(id) ?: throw NotFoundException("User ${id} not found")
        if (user.name == name) return null

        user.name = name
        return repository.save(user)
            .also { log.info("User ${it.id} updated.") }
    }

    fun delete(id: Long): Boolean {
        val user = repository.findByIdOrNull(id) ?: return false

        if (user.isAdmin() && repository.findByRole("ADMIN").size == 1) {
            throw BadRequestException("Cannot delete the last administrator!")
        }

        repository.delete(user)
        log.info("User ${id} deleted.")
        return true
    }

    @Transactional
    fun addRole(id: Long, roleName: String): Boolean {
        val upperRole = roleName.uppercase()
        val user = findById(id)
        if (user.roles.any { it.name == upperRole }) return false

        val role = roleRepository.findByName(upperRole) ?: throw BadRequestException("Role $upperRole not found")

        user.roles.add(role)
        repository.save(user)
        log.info("Role $upperRole granted to ${user.id}.")
        return true
    }

    fun login(email: String, password: String): LoginResponse {
        val user = repository.findByEmail(email) ?: throw UnauthorizedException("User not found")
        if (user.password != password) throw UnauthorizedException("Wrong password")

        log.info("User ${user.id} logged in.")
        return LoginResponse(
            token = jwt.createToken(user),
            user = UserResponse(user)
        )
    }

    fun saveAvatar(id: Long, avatar: MultipartFile): String {
        val user = findById(id)
        user.avatar = avatarService.save(user, avatar)
        repository.save(user)
        return avatarService.urlFor(user.avatar)
    }

    companion object {
        val log = LoggerFactory.getLogger(UserService::class.java)
    }
}