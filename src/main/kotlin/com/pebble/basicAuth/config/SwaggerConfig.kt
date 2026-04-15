package com.pebble.basicAuth.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val jwtSchemeName = "JWT Auth"
        
        val securityRequirement = SecurityRequirement().addList(jwtSchemeName)
        
        val components = Components()
            .addSecuritySchemes(jwtSchemeName, SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"))

        return OpenAPI()
            .info(Info()
                .title("Basic-Auth API Specification")
                .description("인증, 소셜 로그인, 대기열 시스템 API 명세서입니다.")
                .version("v1.0.0"))
            .addSecurityItem(securityRequirement)
            .components(components)
    }
}
