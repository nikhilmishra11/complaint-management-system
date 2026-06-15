package com.example.cms.controller;

import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Validated
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "/login"})
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam @Email String email,
                        @RequestParam @NotBlank String password,
                        HttpSession session,
                        Model model) {
        return userService.login(email, password)
                .map(user -> {
                    session.setAttribute("loggedInUser", user);
                    return "redirect:/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invalid email or password. Please try again or register.");
                    return "auth/login";
                });
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", new Role[]{Role.CUSTOMER});
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", new Role[]{Role.CUSTOMER});
            return "auth/register";
        }
        try {
            user.setRole(Role.CUSTOMER);
            userService.register(user);
            model.addAttribute("success", "Registration successful. Please login.");
            return "auth/login";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("roles", new Role[]{Role.CUSTOMER});
            return "auth/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String resetPassword(@RequestParam @Email String email,
                                @RequestParam @Size(min = 6, max = 60) String password,
                                Model model) {
        try {
            userService.resetPassword(email, password);
            model.addAttribute("success", "Password reset successful. Please login.");
            return "auth/login";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/forgot-password";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
