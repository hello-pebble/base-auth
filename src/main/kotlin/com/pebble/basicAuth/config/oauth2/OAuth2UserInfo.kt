package com.pebble.basicAuth.config.oauth2

abstract class OAuth2UserInfo(val attributes: Map<String, Any>) {
    abstract val id: String
    
    val name: String?
        get() = attributes["name"] as? String
        
    val email: String?
        get() = attributes["email"] as? String
}
