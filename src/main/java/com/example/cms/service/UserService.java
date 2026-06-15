package com.example.cms.service;

import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(User user) {
        try {
            userRepository.findByEmail(user.getEmail()).ifPresent(existing -> {
                throw new IllegalArgumentException("Email is already registered");
            });
            return userRepository.save(user);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to register user", ex);
        }
    }

    public Optional<User> login(String email, String password) {
        try {
            return userRepository.findByEmail(email)
                    .filter(user -> user.getPassword().equals(password));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to login user", ex);
        }
    }

    @Transactional
    public void resetPassword(String email, String password) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("No user found with this email"));
            user.setPassword(password);
            userRepository.save(user);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to reset password", ex);
        }
    }

    public List<User> findEmployees() {
        return userRepository.findByRole(Role.EMPLOYEE);
    }

    public List<User> findCustomers() {
        return userRepository.findByRole(Role.CUSTOMER);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
