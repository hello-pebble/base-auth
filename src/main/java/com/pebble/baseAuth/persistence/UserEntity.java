package com.pebble.baseAuth.persistence;

import com.pebble.baseAuth.domain.User;
import com.pebble.baseAuth.domain.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public UserEntity(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = (role != null) ? role : UserRole.ROLE_USER;
    }

    public static UserEntity from(User user) {
        UserEntity entity = new UserEntity(user.getUsername(), user.getPassword(), user.getRole());
        entity.id = user.getId();
        entity.deletedAt = user.getDeletedAt();
        return entity;
    }

    public User toDomain() {
        return new User(this.id, this.username, this.password, this.role, this.deletedAt);
    }
}
