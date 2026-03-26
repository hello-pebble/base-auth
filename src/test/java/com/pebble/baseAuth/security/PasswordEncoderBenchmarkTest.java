package com.pebble.baseAuth.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.util.StopWatch;

import java.util.LinkedHashMap;
import java.util.Map;

class PasswordEncoderBenchmarkTest {

    private static final String RAW_PASSWORD = "Password123!@#";
    private static final int ITERATIONS = 5; // 정확도를 위해 5회 반복 측정

    @Test
    @DisplayName("암호화 알고리즘별 성능 비교 측정 (BCrypt, Argon2, SCrypt, PBKDF2)")
    void compareEncryptionSpeed() {
        Map<String, PasswordEncoder> encoders = new LinkedHashMap<>();
        encoders.put("BCrypt (Strength 10)", new BCryptPasswordEncoder());
        encoders.put("Argon2 (Spring 5.8 Defaults)", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("SCrypt (Spring 5.8 Defaults)", SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8());
        encoders.put("PBKDF2 (Spring 5.8 Defaults)", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());

        StopWatch stopWatch = new StopWatch("PasswordEncoder Benchmark");

        System.out.println("=== 암호화 알고리즘 성능 비교 시작 (반복 횟수: " + ITERATIONS + ") ===\n");

        encoders.forEach((name, encoder) -> {
            stopWatch.start(name + " - encode");
            String encoded = "";
            for (int i = 0; i < ITERATIONS; i++) {
                encoded = encoder.encode(RAW_PASSWORD);
            }
            stopWatch.stop();

            stopWatch.start(name + " - matches");
            for (int i = 0; i < ITERATIONS; i++) {
                encoder.matches(RAW_PASSWORD, encoded);
            }
            stopWatch.stop();
        });

        printResults(stopWatch);
    }

    private void printResults(StopWatch stopWatch) {
        System.out.println(stopWatch.prettyPrint());
        
        System.out.println("-----------------------------------------");
        System.out.printf("%-30s | %-15s%n", "Task Name", "Total Time (ms)");
        System.out.println("-----------------------------------------");
        for (StopWatch.TaskInfo task : stopWatch.getTaskInfo()) {
            System.out.printf("%-30s | %-15.3f%n", 
                task.getTaskName(), 
                task.getTimeNanos() / 1_000_000.0);
        }
        System.out.println("-----------------------------------------");
    }
}
