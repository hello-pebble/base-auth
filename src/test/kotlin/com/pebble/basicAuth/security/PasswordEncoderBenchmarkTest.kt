package com.pebble.basicAuth.security

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder
import org.springframework.util.StopWatch

class PasswordEncoderBenchmarkTest {

    private companion object {
        const val RAW_PASSWORD = "Password123!@#"
        const val ITERATIONS = 5 // ?�확?��? ?�해 5??반복 측정
    }

    @Test
    @DisplayName("?�호???�고리즘�??�능 비교 측정 (BCrypt, SCrypt, PBKDF2)")
    fun compareEncryptionSpeed() {
        val encoders = linkedMapOf<String, PasswordEncoder>(
            "BCrypt (Strength 10 - Standard)" to BCryptPasswordEncoder(),
            "SCrypt (Spring 5.8 Defaults)" to SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8(),
            "PBKDF2 (Spring 5.8 Defaults)" to Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()
        )

        val stopWatch = StopWatch("PasswordEncoder Benchmark")

        println("=== ?�호???�고리즘 ?�능 비교 ?�작 (반복 ?�수: $ITERATIONS) ===\n")

        encoders.forEach { (name, encoder) ->
            var encoded = ""
            
            stopWatch.start("$name - encode")
            repeat(ITERATIONS) {
                encoded = encoder.encode(RAW_PASSWORD)
            }
            stopWatch.stop()

            stopWatch.start("$name - matches")
            repeat(ITERATIONS) {
                encoder.matches(RAW_PASSWORD, encoded)
            }
            stopWatch.stop()
        }

        printResults(stopWatch)
    }

    private fun printResults(stopWatch: StopWatch) {
        println(stopWatch.prettyPrint())
        
        println("-----------------------------------------")
        println("%-30s | %-15s".format("Task Name", "Total Time (ms)"))
        println("-----------------------------------------")
        for (task in stopWatch.taskInfo) {
            println("%-30s | %-15.3f".format(
                task.taskName, 
                task.timeNanos / 1_000_000.0)
            )
        }
        println("-----------------------------------------")
    }
}
