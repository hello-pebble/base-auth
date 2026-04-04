package com.pebble.baseAuth.config.oauth2;

import com.pebble.baseAuth.domain.User;
import com.pebble.baseAuth.domain.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomOAuth2UserService 단위 테스트")
class CustomOAuth2UserServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomOAuth2UserService oAuth2UserService;

    @Test
    @DisplayName("OAuth2UserInfoFactory_구글객체생성_확인")
    void oAuth2UserInfoFactory_Google_ReturnsGoogleUserInfo() {
        // given
        Map<String, Object> attributes = Map.of(
                "sub", "123456789",
                "name", "홍길동",
                "email", "hong@gmail.com"
        );

        // when
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo("google", attributes);

        // then
        assertThat(userInfo).isInstanceOf(GoogleOAuth2UserInfo.class);
        assertThat(userInfo.getId()).isEqualTo("123456789");
        assertThat(userInfo.getName()).isEqualTo("홍길동");
        assertThat(userInfo.getEmail()).isEqualTo("hong@gmail.com");
    }

    @Test
    @DisplayName("OAuth2UserInfoFactory_깃허브객체생성_확인")
    void oAuth2UserInfoFactory_Github_ReturnsGithubUserInfo() {
        // given
        Map<String, Object> attributes = Map.of(
                "id", 987654321,
                "name", "김철수",
                "email", "kim@github.com"
        );

        // when
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo("github", attributes);

        // then
        assertThat(userInfo).isInstanceOf(GitHubOAuth2UserInfo.class);
        assertThat(userInfo.getId()).isEqualTo("987654321");
        assertThat(userInfo.getName()).isEqualTo("김철수");
        assertThat(userInfo.getEmail()).isEqualTo("kim@github.com");
    }
}
