package com.pebble.basicAuth.config.oauth2

object OAuth2UserInfoFactory {
    @JvmStatic
    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return when (registrationId.lowercase()) {
            "google" -> GoogleOAuth2UserInfo(attributes)
            else -> throw IllegalArgumentException("ì§€?í•˜ì§€ ?ŠëŠ” ?Œì…œ ë¡œê·¸???œê³µ?ì…?ˆë‹¤: $registrationId")
        }
    }
}
