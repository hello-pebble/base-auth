package com.pebble.basicAuth.persistence

import com.pebble.basicAuth.domain.User
import com.pebble.basicAuth.domain.UserRole
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 30)
    var username: String,

    @Column(unique = true, length = 100)
    var email: String? = null,

    @Column(length = 255)
    var password: String? = null,

    @Column(length = 20)
    var provider: String? = null,

    @Column(length = 50)
    var providerId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var role: UserRole = UserRole.ROLE_USER,

    var deletedAt: LocalDateTime? = null
) {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: LocalDateTime

    // Secondary constructor to match Java's constructor
    constructor(username: String, email: String?, password: String?, provider: String?, providerId: String?, role: UserRole?) : 
        this(null, username, email, password, provider, providerId, role ?: UserRole.ROLE_USER, null)

    companion object {
        @JvmStatic
        fun from(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                username = user.username,
                email = user.email,
                password = user.password,
                provider = user.provider,
                providerId = user.providerId,
                role = user.role,
                deletedAt = user.deletedAt
            )
        }
    }

    fun toDomain(): User {
        return User(
            id = this.id,
            username = this.username,
            email = this.email,
            password = this.password,
            provider = this.provider,
            providerId = this.providerId,
            role = this.role,
            deletedAt = this.deletedAt
        )
    }
}
