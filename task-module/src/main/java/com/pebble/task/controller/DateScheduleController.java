package com.pebble.task.controller;

import com.pebble.task.domain.DateSchedule;
import com.pebble.task.domain.DateScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dates")
@RequiredArgsConstructor
public class DateScheduleController {
    private final DateScheduleService dateScheduleService;

    @PostMapping
    public ResponseEntity<DateSchedule> createSchedule(
            @RequestBody DateSchedule schedule,
            @AuthenticationPrincipal Jwt jwt) {
        schedule.setHostUserId(jwt.getSubject());
        return ResponseEntity.ok(dateScheduleService.createDateSchedule(schedule));
    }

    @GetMapping
    public ResponseEntity<List<DateSchedule>> getMySchedules(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(dateScheduleService.getMyDateSchedules(jwt.getSubject()));
    }

    @PatchMapping("/{scheduleId}")
    public ResponseEntity<DateSchedule> updateStatus(
            @PathVariable Long scheduleId,
            @RequestParam String status,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(dateScheduleService.updateScheduleStatus(scheduleId, jwt.getSubject(), status));
    }
}
