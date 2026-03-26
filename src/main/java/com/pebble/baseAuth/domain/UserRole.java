package com.pebble.baseAuth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    // 일반 사용자 권한
    ROLE_USER("일반 사용자"),
    
    // 관리자 권한
    ROLE_ADMIN("시스템 관리자");

    private final String description;
}
