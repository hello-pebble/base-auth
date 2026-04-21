package com.pebble.basicAuth.domain

import java.time.LocalDateTime

data class User(
    val id: Long? = null,
    val username: String,
    val email: String? = null,
    val password: String? = null,
    val provider: String? = null,
    val providerId: String? = null,
    val role: UserRole = UserRole.ROLE_USER,
    var deletedAt: LocalDateTime? = null
) {
    // Secondary constructors to maintain compatibility with Java calls if any remain
    constructor(username: String, password: String?) : this(null, username, null, password, null, null, UserRole.ROLE_USER, null)
    constructor(username: String, password: String?, role: UserRole) : this(null, username, null, password, null, null, role, null)

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    fun isDeleted(): Boolean {
        return this.deletedAt != null
    }

    companion object {
        /**
         * 소셜 로그인을 위한 신규 사용자 생성 (정적 팩토리 메서드)
         */
        @JvmStatic
        fun createSocialUser(username: String, email: String, provider: String, providerId: String): User {
            return User(
                id = null,
                username = username,
                email = email,
                password = null, // 소셜 사용자는 비밀번호 없음
                provider = provider,
                providerId = providerId,
                role = UserRole.ROLE_USER,
                deletedAt = null
            )
        }
    }
}
