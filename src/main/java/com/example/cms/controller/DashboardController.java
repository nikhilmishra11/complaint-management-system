package com.example.cms.controller;

import com.example.cms.model.Role;
import com.example.cms.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        if (user.getRole() == Role.ADMIN) {
            return "redirect:/admin/complaints";
        }
        if (user.getRole() == Role.EMPLOYEE) {
            return "redirect:/employee/complaints";
        }
        return "redirect:/user/complaints";
    }
}
