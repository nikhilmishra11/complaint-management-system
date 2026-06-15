package com.example.cms.controller.api;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.User;
import com.example.cms.service.ComplaintService;
import com.example.cms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintApiController {

    private final ComplaintService complaintService;
    private final UserService userService;

    public ComplaintApiController(ComplaintService complaintService, UserService userService) {
        this.complaintService = complaintService;
        this.userService = userService;
    }

    @GetMapping
    public List<Complaint> all() {
        return complaintService.loadComplaints();
    }

    @GetMapping("/{id}")
    public Complaint get(@PathVariable Long id) {
        return complaintService.getComplaint(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Complaint create(@RequestParam Long customerId, @Valid @RequestBody Complaint complaint) {
        User customer = userService.findById(customerId);
        complaint.setCustomer(customer);
        return complaintService.addComplaint(complaint);
    }

    @PutMapping("/{id}")
    public Complaint update(@PathVariable Long id, @Valid @RequestBody Complaint complaint) {
        return complaintService.updateComplaint(id, complaint);
    }

    @PatchMapping("/{id}/status")
    public Complaint updateStatus(@PathVariable Long id, @RequestParam ComplaintStatus status) {
        Complaint complaint = complaintService.getComplaint(id);
        complaint.setStatus(status);
        return complaintService.updateComplaint(id, complaint);
    }

    @PatchMapping("/{id}/comments")
    public Complaint updateComments(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Complaint complaint = complaintService.getComplaint(id);
        complaint.setComments(body.getOrDefault("comments", ""));
        return complaintService.updateComplaint(id, complaint);
    }

    @PatchMapping("/{id}/feedback")
    public Complaint feedback(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Complaint complaint = complaintService.getComplaint(id);
        complaint.setFeedback(body.getOrDefault("feedback", ""));
        return complaintService.updateComplaint(id, complaint);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
    }
}
