package com.pebble.baseAuth.controller;

import com.pebble.baseAuth.domain.User;
import com.pebble.baseAuth.domain.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @PostMapping("/api/v1/users/signup")
    public ResponseEntity<UserResponse> signUp(@RequestBody UserSignUpRequest request) {
        UserResponse response = UserResponse.from(userService.signUp(request.username(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/users/admin/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("관리자 인증 성공! 당신은 시스템 관리자입니다.");
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<UserResponse> login(@ModelAttribute LoginRequest request,
                                                HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        securityContextRepository.saveContext(securityContext, httpRequest, httpResponse);

        UserResponse response = UserResponse.from(userService.findByUsername(request.username()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/users/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        UserResponse response = UserResponse.from(userService.findByUsername(authentication.getName()));
        return ResponseEntity.ok(response);
    }

    public record UserSignUpRequest(String username, String password) {}
    public record LoginRequest(String username, String password) {}
    public record UserResponse(Long id, String username) {
        public static UserResponse from(User user) {
            return new UserResponse(user.getId(), user.getUsername());
        }
    }
}
