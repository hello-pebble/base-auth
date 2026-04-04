package com.pebble.baseAuth.config.oauth2

import com.pebble.baseAuth.domain.UserService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userService: UserService
) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        // [CTO 리뷰 반영] 외부 IdP API 호출 시 DB 트랜잭션을 잡지 않도록 수정
        val oAuth2User = super.loadUser(userRequest)

        val registrationId = userRequest.clientRegistration.registrationId
        val attributes = oAuth2User.attributes

        val oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes)

        // DB 작업만 트랜잭션 범위 내에서 수행되도록 서비스에 위임
        val user = userService.saveOrUpdateSocialUser(
            registrationId,
            oAuth2UserInfo.id,
            oAuth2UserInfo.email,
            oAuth2UserInfo.name
        )

        return CustomOAuth2User(user, attributes)
    }
}
