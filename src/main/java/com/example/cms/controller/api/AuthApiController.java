package com.example.cms.controller.api;

import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserService userService;

    public AuthApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public User login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request.email(), request.password())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody User user) {
        user.setRole(Role.CUSTOMER);
        return userService.register(user);
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.email(), request.password());
        return Map.of("message", "Password reset successful");
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record ResetPasswordRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, max = 60) String password
    ) {
    }
}
