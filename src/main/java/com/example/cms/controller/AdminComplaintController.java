package com.example.cms.controller;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
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
@RequestMapping("/admin/complaints")
public class AdminComplaintController {

    private final ComplaintService complaintService;

    public AdminComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    public String list(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("complaints", complaintService.loadComplaints());
        return "admin/complaints";
    }

    @GetMapping("/new")
    public String createPage(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("complaint", new Complaint());
        model.addAttribute("statuses", ComplaintStatus.values());
        return "admin/complaint-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Complaint complaint,
                         BindingResult result,
                         HttpSession session,
                         Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            model.addAttribute("statuses", ComplaintStatus.values());
            return "admin/complaint-form";
        }
        complaint.setCustomer(user);
        complaintService.addComplaint(complaint);
        return "redirect:/admin/complaints";
    }

    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("complaint", complaintService.getComplaint(id));
        model.addAttribute("statuses", ComplaintStatus.values());
        return "admin/complaint-form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute Complaint complaint,
                         BindingResult result,
                         HttpSession session,
                         Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            model.addAttribute("statuses", ComplaintStatus.values());
            return "admin/complaint-form";
        }
        complaintService.updateComplaint(id, complaint);
        return "redirect:/admin/complaints";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ComplaintStatus status,
                               HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        Complaint complaint = complaintService.getComplaint(id);
        complaint.setStatus(status);
        complaintService.updateComplaint(id, complaint);
        return "redirect:/admin/complaints";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        complaintService.deleteComplaint(id);
        return "redirect:/admin/complaints";
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && user.getRole() == Role.ADMIN;
    }
}
