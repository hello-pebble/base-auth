package com.pebble.baseAuth.domain

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
    fun signUp(username: String, password: String): User {
        if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
            throw UserException("이미 존재하는 사용자명입니다.")
        }

        val encodedPassword = passwordEncoder.encode(password)

        return userRepository.save(User(username, encodedPassword, UserRole.ROLE_USER))
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
            .map { existingUser ->
                // 필요한 경우 정보 업데이트 로직 추가 (예: 이름 변경 등)
                existingUser
            }
            .orElseGet {
                var username = if (!email.isNullOrEmpty()) email else "${provider}_$providerId"

                if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
                    username = "${username}_${UUID.randomUUID().toString().substring(0, 5)}"
                }

                userRepository.save(User.createSocialUser(username, provider, providerId))
            }
    }
}
