package com.pebble.baseAuth.config.oauth2

abstract class OAuth2UserInfo(val attributes: Map<String, Any>) {
    abstract val id: String
    abstract val name: String?
    abstract val email: String?
}
