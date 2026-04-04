package com.pebble.baseAuth.config.oauth2

class GitHubOAuth2UserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override val id: String
        get() = attributes["id"].toString()

    override val name: String?
        get() = attributes["name"] as? String

    override val email: String?
        get() = attributes["email"] as? String
}
