package com.example.cms.controller;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.service.AssignationService;
import com.example.cms.service.ComplaintService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AssignmentController {

    private final AssignationService assignationService;
    private final ComplaintService complaintService;

    public AssignmentController(AssignationService assignationService, ComplaintService complaintService) {
        this.assignationService = assignationService;
        this.complaintService = complaintService;
    }

    @GetMapping("/admin/assignments")
    public String assignments(HttpSession session, Model model) {
        if (!hasRole(session, Role.ADMIN)) {
            return "redirect:/login";
        }
        model.addAttribute("complaints", complaintService.loadComplaints());
        model.addAttribute("employees", assignationService.getAvailable());
        return "admin/assignments";
    }

    @PostMapping("/admin/assignments")
    public String assign(@RequestParam Long complaintId,
                         @RequestParam Long employeeId,
                         HttpSession session,
                         Model model) {
        if (!hasRole(session, Role.ADMIN)) {
            return "redirect:/login";
        }
        try {
            assignationService.assignComplaint(complaintId, employeeId);
            return "redirect:/admin/assignments";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("complaints", complaintService.loadComplaints());
            model.addAttribute("employees", assignationService.getAvailable());
            return "admin/assignments";
        }
    }

    @GetMapping("/employee/complaints")
    public String employeeComplaints(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || user.getRole() != Role.EMPLOYEE) {
            return "redirect:/login";
        }
        model.addAttribute("complaints", complaintService.findByAssignedEmployee(user));
        model.addAttribute("statuses", ComplaintStatus.values());
        return "employee/complaints";
    }

    @PostMapping("/employee/complaints/{id}/update")
    public String employeeUpdate(@PathVariable Long id,
                                 @RequestParam ComplaintStatus status,
                                 @RequestParam(required = false) String employeeRemarks,
                                 HttpSession session) {
        if (!hasRole(session, Role.EMPLOYEE)) {
            return "redirect:/login";
        }
        Complaint complaint = complaintService.getComplaint(id);
        complaint.setStatus(status);
        complaint.setEmployeeRemarks(employeeRemarks);
        complaintService.updateComplaint(id, complaint);
        return "redirect:/employee/complaints";
    }

    private boolean hasRole(HttpSession session, Role role) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && user.getRole() == role;
    }
}
