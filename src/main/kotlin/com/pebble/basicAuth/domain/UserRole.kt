package com.pebble.basicAuth.domain

enum class UserRole(val description: String) {
    // 일반 사용자 권한
    ROLE_USER("일반 사용자"),

    // 관리자 권한
    ROLE_ADMIN("시스템 관리자");
}

