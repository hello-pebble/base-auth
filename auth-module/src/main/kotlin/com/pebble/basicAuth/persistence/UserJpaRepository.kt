package com.pebble.basicAuth.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserJpaRepository : JpaRepository<UserEntity, Long> {

    fun existsByUsernameAndDeletedAtIsNull(username: String): Boolean

    fun findByUsernameAndDeletedAtIsNull(username: String): Optional<UserEntity>

    fun findByEmailAndDeletedAtIsNull(email: String): Optional<UserEntity>

    fun existsByEmailAndDeletedAtIsNull(email: String): Boolean

    fun findByProviderAndProviderIdAndDeletedAtIsNull(provider: String, providerId: String): Optional<UserEntity>

}
