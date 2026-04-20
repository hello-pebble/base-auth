package com.pebble.basicAuth.persistence

import com.pebble.basicAuth.domain.User
import com.pebble.basicAuth.domain.UserRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class UserRepositoryImpl(private val userJpaRepository: UserJpaRepository) : UserRepository {

    override fun existsByUsernameAndDeletedAtIsNull(username: String): Boolean {
        return userJpaRepository.existsByUsernameAndDeletedAtIsNull(username)
    }

    override fun findByUsernameAndDeletedAtIsNull(username: String): Optional<User> {
        return userJpaRepository.findByUsernameAndDeletedAtIsNull(username)
            .map { it.toDomain() }
    }

    override fun findByProviderAndProviderIdAndDeletedAtIsNull(provider: String, providerId: String): Optional<User> {
        return userJpaRepository.findByProviderAndProviderIdAndDeletedAtIsNull(provider, providerId)
            .map { it.toDomain() }
    }

    override fun findById(id: Long): Optional<User> {
        return userJpaRepository.findById(id).map { it.toDomain() }
    }

    override fun save(user: User): User {
        val entity = UserEntity.from(user)
        return userJpaRepository.save(entity).toDomain()
    }
}
