package com.example.cms.controller.api;

import com.example.cms.model.Complaint;
import com.example.cms.model.User;
import com.example.cms.service.ComplaintService;
import com.example.cms.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final ComplaintService complaintService;
    private final UserService userService;

    public UserApiController(ComplaintService complaintService, UserService userService) {
        this.complaintService = complaintService;
        this.userService = userService;
    }

    @GetMapping("/{userId}/complaints")
    public List<Complaint> userComplaints(@PathVariable Long userId) {
        User user = userService.findById(userId);
        return complaintService.findByCustomer(user);
    }
}
