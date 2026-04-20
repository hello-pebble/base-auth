package com.pebble.basicAuth.domain

import java.util.Optional

interface UserRepository {

    fun existsByUsernameAndDeletedAtIsNull(username: String): Boolean

    fun findByUsernameAndDeletedAtIsNull(username: String): Optional<User>

    fun findByProviderAndProviderIdAndDeletedAtIsNull(provider: String, providerId: String): Optional<User>

    fun findById(id: Long): Optional<User>

    fun save(user: User): User

}
