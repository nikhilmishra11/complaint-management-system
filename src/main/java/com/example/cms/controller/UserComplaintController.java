package com.example.cms.controller;

import com.example.cms.model.Complaint;
import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.service.ComplaintService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user/complaints")
public class UserComplaintController {

    private final ComplaintService complaintService;

    public UserComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.CUSTOMER) {
            return "redirect:/login";
        }
        model.addAttribute("complaints", complaintService.findByCustomer(user));
        return "user/complaints";
    }

    @GetMapping("/new")
    public String createPage(HttpSession session, Model model) {
        if (!isCustomer(session)) {
            return "redirect:/login";
        }
        model.addAttribute("complaint", new Complaint());
        return "user/complaint-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Complaint complaint,
                         BindingResult result,
                         HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.CUSTOMER) {
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            return "user/complaint-form";
        }
        complaint.setCustomer(user);
        complaintService.addComplaint(complaint);
        return "redirect:/user/complaints";
    }

    @PostMapping("/{id}/comments")
    public String updateComments(@PathVariable Long id,
                                 @RequestParam String comments,
                                 HttpSession session) {
        if (!isCustomer(session)) {
            return "redirect:/login";
        }
        Complaint complaint = complaintService.getComplaint(id);
        complaint.setComments(comments);
        complaintService.updateComplaint(id, complaint);
        return "redirect:/user/complaints";
    }

    @PostMapping("/{id}/feedback")
    public String feedback(@PathVariable Long id,
                           @RequestParam String feedback,
                           HttpSession session) {
        if (!isCustomer(session)) {
            return "redirect:/login";
        }
        Complaint complaint = complaintService.getComplaint(id);
        complaint.setFeedback(feedback);
        complaintService.updateComplaint(id, complaint);
        return "redirect:/user/complaints";
    }

    private boolean isCustomer(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && user.getRole() == Role.CUSTOMER;
    }
}
