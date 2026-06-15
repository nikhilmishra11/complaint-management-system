package com.example.cms.controller.api;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.User;
import com.example.cms.service.AssignationService;
import com.example.cms.service.ComplaintService;
import com.example.cms.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AssignationApiController {

    private final AssignationService assignationService;
    private final ComplaintService complaintService;
    private final UserService userService;

    public AssignationApiController(AssignationService assignationService,
                                    ComplaintService complaintService,
                                    UserService userService) {
        this.assignationService = assignationService;
        this.complaintService = complaintService;
        this.userService = userService;
    }

    @GetMapping("/assignments/available-employees")
    public List<User> availableEmployees() {
        return assignationService.getAvailable();
    }

    @PostMapping("/assignments")
    public Complaint assign(@RequestParam Long complaintId, @RequestParam Long employeeId) {
        return assignationService.assignComplaint(complaintId, employeeId);
    }

    @GetMapping("/employees/{employeeId}/complaints")
    public List<Complaint> employeeComplaints(@PathVariable Long employeeId) {
        User employee = userService.findById(employeeId);
        return complaintService.findByAssignedEmployee(employee);
    }

    @PatchMapping("/employees/complaints/{id}")
    public Complaint employeeUpdate(@PathVariable Long id,
                                    @RequestParam ComplaintStatus status,
                                    @RequestBody Map<String, String> body) {
        Complaint complaint = complaintService.getComplaint(id);
        complaint.setStatus(status);
        complaint.setEmployeeRemarks(body.getOrDefault("employeeRemarks", ""));
        return complaintService.updateComplaint(id, complaint);
    }
}
