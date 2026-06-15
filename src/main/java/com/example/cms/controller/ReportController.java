package com.example.cms.controller;

import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.service.ComplaintService;
import com.example.cms.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ComplaintService complaintService;
    private final UserService userService;

    public ReportController(ComplaintService complaintService, UserService userService) {
        this.complaintService = complaintService;
        this.userService = userService;
    }

    @GetMapping
    public String reports(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("complaints", complaintService.loadComplaints());
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("customers", userService.findCustomers());
        return "reports/index";
    }

    @GetMapping("/status")
    public String byStatus(@RequestParam ComplaintStatus status, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("complaints", complaintService.findByStatus(status));
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("customers", userService.findCustomers());
        model.addAttribute("selectedStatus", status);
        return "reports/index";
    }

    @GetMapping("/date-range")
    public String byDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                              HttpSession session,
                              Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("complaints", complaintService.findByDateRange(startDate, endDate));
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("customers", userService.findCustomers());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "reports/index";
    }

    @GetMapping("/user")
    public String byUser(@RequestParam Long userId, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        User user = userService.findById(userId);
        model.addAttribute("complaints", complaintService.findByCustomer(user));
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("customers", userService.findCustomers());
        model.addAttribute("selectedUser", user);
        return "reports/index";
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && user.getRole() == Role.ADMIN;
    }
}
