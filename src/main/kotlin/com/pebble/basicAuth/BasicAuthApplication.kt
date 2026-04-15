package com.pebble.basicAuth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
class BasicAuthApplication

fun main(args: Array<String>) {
    runApplication<BasicAuthApplication>(*args)
    println("서버 성공 적 API 시작해")
}
