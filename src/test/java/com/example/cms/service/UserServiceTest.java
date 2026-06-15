package com.example.cms.service;

import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    void registerSavesNewUser() {
        User user = user("new@cms.com", Role.CUSTOMER);
        when(userRepository.findByEmail("new@cms.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User saved = userService.register(user);

        assertThat(saved).isSameAs(user);
        verify(userRepository).save(user);
    }

    @Test
    void registerRejectsDuplicateEmail() {
        User user = user("dup@cms.com", Role.CUSTOMER);
        when(userRepository.findByEmail("dup@cms.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.register(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email is already registered");
    }

    @Test
    void loginReturnsUserForMatchingPassword() {
        User user = user("login@cms.com", Role.ADMIN);
        user.setPassword("secret123");
        when(userRepository.findByEmail("login@cms.com")).thenReturn(Optional.of(user));

        assertThat(userService.login("login@cms.com", "secret123")).contains(user);
        assertThat(userService.login("login@cms.com", "bad")).isEmpty();
    }

    @Test
    void resetPasswordUpdatesExistingUser() {
        User user = user("reset@cms.com", Role.CUSTOMER);
        when(userRepository.findByEmail("reset@cms.com")).thenReturn(Optional.of(user));

        userService.resetPassword("reset@cms.com", "newpass123");

        assertThat(user.getPassword()).isEqualTo("newpass123");
        verify(userRepository).save(user);
    }

    @Test
    void resetPasswordRejectsUnknownEmail() {
        when(userRepository.findByEmail("missing@cms.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resetPassword("missing@cms.com", "newpass123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No user found with this email");
    }

    @Test
    void roleQueriesDelegateToRepository() {
        User employee = user("employee@cms.com", Role.EMPLOYEE);
        User customer = user("customer@cms.com", Role.CUSTOMER);
        when(userRepository.findByRole(Role.EMPLOYEE)).thenReturn(List.of(employee));
        when(userRepository.findByRole(Role.CUSTOMER)).thenReturn(List.of(customer));

        assertThat(userService.findEmployees()).containsExactly(employee);
        assertThat(userService.findCustomers()).containsExactly(customer);
    }

    @Test
    void findByIdReturnsUserOrThrows() {
        User user = user("find@cms.com", Role.CUSTOMER);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        assertThat(userService.findById(10L)).isSameAs(user);
        assertThatThrownBy(() -> userService.findById(20L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");
    }

    private User user(String email, Role role) {
        User user = new User();
        user.setName("Test User");
        user.setEmail(email);
        user.setPassword("password");
        user.setPhone("1234567890");
        user.setRole(role);
        return user;
    }
}
