package br.pucpr.authserver.projects

import br.pucpr.authserver.users.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import java.time.LocalDateTime

@Entity
class Project(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    var description: String = "",

    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToMany
    @JoinTable(
        name = "ProjectMembers",
        joinColumns = [JoinColumn(name = "idProject")],
        inverseJoinColumns = [JoinColumn(name = "idUser")]
    )
    var members: MutableSet<User> = mutableSetOf()
)
