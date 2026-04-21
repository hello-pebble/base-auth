package com.pebble.task.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DateScheduleRepository extends JpaRepository<DateSchedule, Long> {
    List<DateSchedule> findAllByHostUserIdOrGuestUserId(String hostUserId, String guestUserId);
}
