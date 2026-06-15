package com.example.cms.controller.api;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.User;
import com.example.cms.service.ComplaintService;
import com.example.cms.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportApiController {

    private final ComplaintService complaintService;
    private final UserService userService;

    public ReportApiController(ComplaintService complaintService, UserService userService) {
        this.complaintService = complaintService;
        this.userService = userService;
    }

    @GetMapping("/status")
    public List<Complaint> byStatus(@RequestParam ComplaintStatus status) {
        return complaintService.findByStatus(status);
    }

    @GetMapping("/date-range")
    public List<Complaint> byDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return complaintService.findByDateRange(startDate, endDate);
    }

    @GetMapping("/user")
    public List<Complaint> byUser(@RequestParam Long userId) {
        User user = userService.findById(userId);
        return complaintService.findByCustomer(user);
    }
}
