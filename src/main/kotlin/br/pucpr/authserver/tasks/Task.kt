package br.pucpr.authserver.tasks

import br.pucpr.authserver.projects.Project
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Task(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var title: String,

    var description: String = "",

    @Enumerated(EnumType.STRING)
    var status: TaskStatus = TaskStatus.BACKLOG,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "idProject", nullable = false)
    val project: Project
)
