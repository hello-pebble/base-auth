package com.pebble.baseAuth.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signUp(String username, String password) {

        if (userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
            throw new UserException("이미 존재하는 사용자명입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        return userRepository.save(new User(username, encodedPassword, UserRole.ROLE_USER));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));
    }

}
