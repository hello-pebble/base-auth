package com.pebble.basicAuth.config.oauth2

import com.pebble.basicAuth.domain.UserService
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
        // [CTO ë¦¬ë·° ë°˜ì˜] ?¸ë? IdP API ?¸ì¶œ ??DB ?¸ëœ??…˜???¡ì? ?Šë„ë¡??˜ì •
        val oAuth2User = super.loadUser(userRequest)

        val registrationId = userRequest.clientRegistration.registrationId
        val attributes = oAuth2User.attributes

        val oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes)

        // DB ?‘ì—…ë§??¸ëœ??…˜ ë²”ìœ„ ?´ì—???˜í–‰?˜ë„ë¡??œë¹„?¤ì— ?„ì„
        val user = userService.saveOrUpdateSocialUser(
            registrationId,
            oAuth2UserInfo.id,
            oAuth2UserInfo.email,
            oAuth2UserInfo.name
        )

        return CustomOAuth2User(user, attributes)
    }
}
