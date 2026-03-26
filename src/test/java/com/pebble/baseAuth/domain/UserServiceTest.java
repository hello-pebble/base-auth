package com.pebble.baseAuth.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입_성공_새로운사용자저장")
    void signUp_NewUser_ReturnsSavedUser() {
        // given
        String username = "testuser";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        User user = new User(username, encodedPassword);

        given(userRepository.existsByUsernameAndDeletedAtIsNull(username)).willReturn(false);
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        User savedUser = userService.signUp(username, password);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(username);
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입_실패_중복된사용자명")
    void signUp_DuplicateUsername_ThrowsUserException() {
        // given
        String username = "existingUser";
        String password = "password123";

        given(userRepository.existsByUsernameAndDeletedAtIsNull(username)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(username, password))
                .isInstanceOf(UserException.class)
                .hasMessage("이미 존재하는 사용자명입니다.");
    }

    @Test
    @DisplayName("사용자조회_성공_사용자반환")
    void findByUsername_ExistingUser_ReturnsUser() {
        // given
        String username = "testuser";
        User user = new User(username, "password");
        given(userRepository.findByUsernameAndDeletedAtIsNull(username)).willReturn(Optional.of(user));

        // when
        User foundUser = userService.findByUsername(username);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("사용자조회_실패_존재하지않는사용자")
    void findByUsername_NonExistingUser_ThrowsUserException() {
        // given
        String username = "nonExistent";
        given(userRepository.findByUsernameAndDeletedAtIsNull(username)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findByUsername(username))
                .isInstanceOf(UserException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }
}
