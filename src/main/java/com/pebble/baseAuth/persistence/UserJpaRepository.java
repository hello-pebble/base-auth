package com.pebble.baseAuth.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByUsernameAndDeletedAtIsNull(String username);

    Optional<UserEntity> findByUsernameAndDeletedAtIsNull(String username);

}
