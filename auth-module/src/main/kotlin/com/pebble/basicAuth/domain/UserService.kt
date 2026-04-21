package com.pebble.basicAuth.domain

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun signUp(username: String, email: String?, password: String): User {
        if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
            throw UserException("이미 존재하는 사용자명입니다.")
        }
        
        if (email != null && userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw UserException("이미 사용 중인 이메일입니다.")
        }

        val encodedPassword = passwordEncoder.encode(password)

        return userRepository.save(User(
            id = null,
            username = username,
            email = email,
            password = encodedPassword,
            provider = null,
            providerId = null,
            role = UserRole.ROLE_USER,
            deletedAt = null
        ))
    }

    @Transactional(readOnly = true)
    fun findByUsername(username: String): User {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
            .orElseThrow { UserException("사용자를 찾을 수 없습니다.") }
    }

    /**
     * 소셜 사용자 정보 저장 또는 업데이트 (DB 트랜잭션 범위 최소화)
     */
    @Transactional
    fun saveOrUpdateSocialUser(provider: String, providerId: String, email: String?, name: String?): User {
        return userRepository.findByProviderAndProviderIdAndDeletedAtIsNull(provider, providerId)
            .map { it } // 필요한 경우 정보 업데이트 로직 추가
            .orElseGet {
                val baseUsername = email?.split("@")?.get(0) ?: "${provider}_$providerId"
                val username = if (userRepository.existsByUsernameAndDeletedAtIsNull(baseUsername)) {
                    "${baseUsername}_${UUID.randomUUID().toString().take(5)}"
                } else {
                    baseUsername
                }

                userRepository.save(User.createSocialUser(
                    username = username,
                    email = email ?: "",
                    provider = provider,
                    providerId = providerId
                ))
            }
    }
}
