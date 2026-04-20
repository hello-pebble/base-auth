package com.pebble.basicAuth.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
@DisplayName("UserService ?¨ìœ„ ?ŒìŠ¤??)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    @DisplayName("?Œì›ê°€???±ê³µ_?ˆë¡œ?´ì‚¬?©ì?€??)
    fun signUp_NewUser_ReturnsSavedUser() {
        // given
        val username = "testuser"
        val password = "password123"
        val encodedPassword = "encodedPassword123"
        val user = User(username, encodedPassword)

        given(userRepository.existsByUsernameAndDeletedAtIsNull(username)).willReturn(false)
        given(passwordEncoder.encode(password)).willReturn(encodedPassword)
        given(userRepository.save(any())).willReturn(user)

        // when
        val savedUser = userService.signUp(username, password)

        // then
        assertThat(savedUser).isNotNull
        assertThat(savedUser.username).isEqualTo(username)
        assertThat(savedUser.password).isEqualTo(encodedPassword)
        verify(userRepository).save(any())
    }

    @Test
    @DisplayName("?Œì›ê°€???¤íŒ¨_ì¤‘ë³µ?œì‚¬?©ìëª?)
    fun signUp_DuplicateUsername_ThrowsUserException() {
        // given
        val username = "existingUser"
        val password = "password123"

        given(userRepository.existsByUsernameAndDeletedAtIsNull(username)).willReturn(true)

        // when & then
        assertThatThrownBy { userService.signUp(username, password) }
            .isInstanceOf(UserException::class.java)
            .hasMessage("?´ë? ì¡´ì¬?˜ëŠ” ?¬ìš©?ëª…?…ë‹ˆ??")
    }

    @Test
    @DisplayName("?¬ìš©?ì¡°???±ê³µ_?¬ìš©?ë°˜??)
    fun findByUsername_ExistingUser_ReturnsUser() {
        // given
        val username = "testuser"
        val user = User(username, "password")
        given(userRepository.findByUsernameAndDeletedAtIsNull(username)).willReturn(Optional.of(user))

        // when
        val foundUser = userService.findByUsername(username)

        // then
        assertThat(foundUser).isNotNull
        assertThat(foundUser.username).isEqualTo(username)
    }

    @Test
    @DisplayName("?¬ìš©?ì¡°???¤íŒ¨_ì¡´ì¬?˜ì??ŠëŠ”?¬ìš©??)
    fun findByUsername_NonExistingUser_ThrowsUserException() {
        // given
        val username = "nonExistent"
        given(userRepository.findByUsernameAndDeletedAtIsNull(username)).willReturn(Optional.empty())

        // when & then
        assertThatThrownBy { userService.findByUsername(username) }
            .isInstanceOf(UserException::class.java)
            .hasMessage("?¬ìš©?ë? ì°¾ì„ ???†ìŠµ?ˆë‹¤.")
    }
}
