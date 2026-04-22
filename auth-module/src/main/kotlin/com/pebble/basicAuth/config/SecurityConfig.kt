package com.pebble.basicAuth.config

import com.pebble.basicAuth.config.oauth2.CustomOAuth2UserService
import com.pebble.basicAuth.config.oauth2.OAuth2SuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val authenticationHandler: CustomAuthenticationHandler,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler
) {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**", "/favicon.ico").permitAll()
                    .requestMatchers("/api/v1/users/signup", "/api/v1/login", "/api/v1/refresh").permitAll()
                    .requestMatchers("/api/v1/waiting-room/**").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    .anyRequest().authenticated()
            }

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

            .oauth2Login { oauth2 ->
                oauth2.userInfoEndpoint { userInfo -> userInfo.userService(customOAuth2UserService) }
                    .successHandler(oAuth2SuccessHandler)
            }

            .exceptionHandling { exception ->
                exception.authenticationEntryPoint(authenticationHandler)
            }
            .logout { logout ->
                logout.logoutUrl("/api/v1/logout")
                    .logoutSuccessHandler(authenticationHandler)
            }
            .headers { headers -> headers.frameOptions { it.disable() } }

        return http.build()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}
