package com.pebble.task.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DateScheduleService {
    private final DateScheduleRepository dateScheduleRepository;

    @Transactional
    public DateSchedule createDateSchedule(DateSchedule schedule) {
        return dateScheduleRepository.save(schedule);
    }

    public List<DateSchedule> getMyDateSchedules(String userId) {
        return dateScheduleRepository.findAllByHostUserIdOrGuestUserId(userId, userId);
    }

    @Transactional
    public DateSchedule updateScheduleStatus(Long scheduleId, String userId, String status) {
        DateSchedule schedule = dateScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        
        if (!schedule.getHostUserId().equals(userId) && !schedule.getGuestUserId().equals(userId)) {
            throw new IllegalStateException("Not authorized to update this schedule");
        }
        
        schedule.setStatus(status);
        return schedule;
    }
}
