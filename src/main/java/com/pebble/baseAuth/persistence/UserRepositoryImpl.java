package com.pebble.baseAuth.persistence;

import com.pebble.baseAuth.domain.User;
import com.pebble.baseAuth.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existsByUsernameAndDeletedAtIsNull(String username) {
        return userJpaRepository.existsByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public Optional<User> findByUsernameAndDeletedAtIsNull(String username) {
        return userJpaRepository.findByUsernameAndDeletedAtIsNull(username)
                .map(UserEntity::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.from(user);
        return userJpaRepository.save(entity).toDomain();
    }
}
