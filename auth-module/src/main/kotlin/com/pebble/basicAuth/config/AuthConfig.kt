package com.pebble.basicAuth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AuthConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        // [CTO Decision] Argon2??? ì?ë³´ìˆ˜ ì¤‘ë‹¨ ë°??’ì? ë¦¬ì†Œ???Œëª¨ë¡??¸í•´
        // ?€ì¤‘ì ?´ê³  ì§€?ì ?¼ë¡œ ê´€ë¦¬ë˜??BCryptë¡??„í™˜?©ë‹ˆ??
        return BCryptPasswordEncoder()
    }
}
