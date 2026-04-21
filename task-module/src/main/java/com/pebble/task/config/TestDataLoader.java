package com.pebble.task.config;

import com.pebble.task.domain.DateSchedule;
import com.pebble.task.domain.DateScheduleRepository;
import com.pebble.task.domain.Task;
import com.pebble.task.domain.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Configuration
@Profile("!test")
public class TestDataLoader {

    @Bean
    CommandLineRunner initTaskData(TaskRepository taskRepository, DateScheduleRepository dateScheduleRepository) {
        return args -> {
            // 1. 일반 사용자(user)를 위한 업무 데이터
            if (taskRepository.findAllByUserId("user").isEmpty()) {
                taskRepository.save(Task.builder()
                        .userId("user")
                        .title("Render 배포 확인하기")
                        .description("모든 모듈이 Render에서 정상 작동하는지 체크")
                        .deadline(LocalDateTime.now().plusDays(1))
                        .build());
                
                taskRepository.save(Task.builder()
                        .userId("user")
                        .title("축하 파티 준비")
                        .description("프로젝트 완성을 축하하는 저녁 약속 잡기")
                        .completed(true)
                        .build());
            }

            // 2. 관리자(admin)를 위한 업무 데이터
            if (taskRepository.findAllByUserId("admin").isEmpty()) {
                taskRepository.save(Task.builder()
                        .userId("admin")
                        .title("시스템 모니터링")
                        .description("트래픽 및 에러 로그 확인")
                        .build());
            }

            // 3. 데이트 일정 샘플 (user <-> guest)
            if (dateScheduleRepository.findAllByHostUserIdOrGuestUserId("user", "user").isEmpty()) {
                dateScheduleRepository.save(DateSchedule.builder()
                        .hostUserId("user")
                        .guestUserId("guest_user_1")
                        .matchingId("match_999")
                        .title("첫 번째 공식 데이트")
                        .description("Render 배포 성공 기념 맛집 탐방")
                        .meetingAt(LocalDateTime.now().plusDays(3))
                        .location("서울 강남구 어딘가")
                        .status("SCHEDULED")
                        .build());
            }

            System.out.println(">>> Task Module Test Data Initialized");
        };
    }
}
