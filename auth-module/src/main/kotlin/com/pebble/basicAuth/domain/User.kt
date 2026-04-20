package com.pebble.basicAuth.domain

import java.time.LocalDateTime

data class User(
    val id: Long? = null,
    val username: String,
    val password: String? = null,
    val provider: String? = null,
    val providerId: String? = null,
    val role: UserRole = UserRole.ROLE_USER,
    var deletedAt: LocalDateTime? = null
) {
    // Secondary constructors to maintain compatibility with Java calls if any remain
    constructor(username: String, password: String?) : this(null, username, password, null, null, UserRole.ROLE_USER, null)
    constructor(username: String, password: String?, role: UserRole) : this(null, username, password, null, null, role, null)

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    fun isDeleted(): Boolean {
        return this.deletedAt != null
    }

    companion object {
        /**
         * ?Œì…œ ë¡œê·¸?¸ì„ ?µí•œ ? ê·œ ?¬ìš©???ì„± (?•ì  ?©í† ë¦?ë©”ì„œ??
         */
        @JvmStatic
        fun createSocialUser(username: String, provider: String, providerId: String): User {
            return User(
                id = null,
                username = username,
                password = null, // ?Œì…œ ?¬ìš©?ëŠ” ë¹„ë?ë²ˆí˜¸ ?†ìŒ
                provider = provider,
                providerId = providerId,
                role = UserRole.ROLE_USER,
                deletedAt = null
            )
        }
    }
}
