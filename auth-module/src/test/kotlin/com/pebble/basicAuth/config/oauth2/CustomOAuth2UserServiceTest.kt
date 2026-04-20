package com.pebble.basicAuth.config.oauth2

import com.pebble.basicAuth.domain.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
@DisplayName("CustomOAuth2UserService Unit Test")
class CustomOAuth2UserServiceTest {

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var oAuth2UserService: CustomOAuth2UserService

    @Test
    @DisplayName("OAuth2UserInfoFactory should create GoogleUserInfo")
    fun oAuth2UserInfoFactory_Google_ReturnsGoogleUserInfo() {
        // given
        val attributes = mapOf(
            "sub" to "123456789",
            "name" to "HongGilDong",
            "email" to "hong@gmail.com"
        )

        // when
        val userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo("google", attributes)

        // then
        assertThat(userInfo).isInstanceOf(GoogleOAuth2UserInfo::class.java)
        assertThat(userInfo.id).isEqualTo("123456789")
        assertThat(userInfo.name).isEqualTo("HongGilDong")
        assertThat(userInfo.email).isEqualTo("hong@gmail.com")
    }
}
