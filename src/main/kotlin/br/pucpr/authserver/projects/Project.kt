package br.pucpr.authserver.projects

import br.pucpr.authserver.users.User
import br.pucpr.authserver.tasks.Task
import jakarta.persistence.*
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
    var members: MutableSet<User> = mutableSetOf(),

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true)
    var tasks: MutableList<Task> = mutableListOf()
)
