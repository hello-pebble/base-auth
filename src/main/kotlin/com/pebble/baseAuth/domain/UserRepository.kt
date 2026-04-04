package com.pebble.baseAuth.domain

import java.util.Optional

interface UserRepository {

    fun existsByUsernameAndDeletedAtIsNull(username: String): Boolean

    fun findByUsernameAndDeletedAtIsNull(username: String): Optional<User>

    fun findByProviderAndProviderIdAndDeletedAtIsNull(provider: String, providerId: String): Optional<User>

    fun save(user: User): User

}
