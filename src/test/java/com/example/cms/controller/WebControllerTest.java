package com.example.cms.controller;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.repository.ComplaintRepository;
import com.example.cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    private User admin;
    private User customer;
    private User employee;
    private Complaint complaint;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString();
        admin = userRepository.save(user("Admin " + suffix, "admin-" + suffix + "@cms.com", Role.ADMIN, true));
        customer = userRepository.save(user("Customer " + suffix, "customer-" + suffix + "@cms.com", Role.CUSTOMER, true));
        employee = userRepository.save(user("Employee " + suffix, "employee-" + suffix + "@cms.com", Role.EMPLOYEE, true));

        complaint = new Complaint();
        complaint.setTitle("Web controller complaint");
        complaint.setDescription("Complaint used by web controller tests.");
        complaint.setCategory("General");
        complaint.setComplaintDate(LocalDate.of(2026, 6, 15));
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setCustomer(customer);
        complaint = complaintRepository.save(complaint);
    }

    @Test
    void authPagesAndActionsWork() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));

        mockMvc.perform(post("/login")
                        .param("email", admin.getEmail())
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));

        mockMvc.perform(post("/login")
                        .param("email", "bad@cms.com")
                        .param("password", "wrongpass"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/login"));

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));

        String email = "registered-" + UUID.randomUUID() + "@cms.com";
        mockMvc.perform(post("/register")
                        .param("name", "Registered User")
                        .param("email", email)
                        .param("phone", "9999999999")
                        .param("password", "password123")
                        .param("role", "CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("success"))
                .andExpect(view().name("auth/login"));

        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgot-password"));

        mockMvc.perform(post("/forgot-password")
                        .param("email", email)
                        .param("password", "newpass123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("success"))
                .andExpect(view().name("auth/login"));

        mockMvc.perform(post("/forgot-password")
                        .param("email", "missing@cms.com")
                        .param("password", "newpass123"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/forgot-password"));

        mockMvc.perform(get("/logout").sessionAttr("loggedInUser", admin))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void dashboardRoutesByRoleAndRejectsAnonymous() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/dashboard").sessionAttr("loggedInUser", admin))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/complaints"));

        mockMvc.perform(get("/dashboard").sessionAttr("loggedInUser", employee))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/complaints"));

        mockMvc.perform(get("/dashboard").sessionAttr("loggedInUser", customer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/complaints"));
    }

    @Test
    void adminComplaintScreensAndActionsWork() throws Exception {
        mockMvc.perform(get("/admin/complaints").sessionAttr("loggedInUser", admin))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/complaints"));

        mockMvc.perform(get("/admin/complaints/new").sessionAttr("loggedInUser", admin))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/complaint-form"));

        mockMvc.perform(post("/admin/complaints")
                        .sessionAttr("loggedInUser", admin)
                        .param("title", "Admin form complaint")
                        .param("description", "Admin created complaint from form.")
                        .param("category", "Admin")
                        .param("complaintDate", "2026-06-15")
                        .param("status", "PENDING")
                        .param("comments", "Admin comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/complaints"));

        mockMvc.perform(get("/admin/complaints/{id}/edit", complaint.getId()).sessionAttr("loggedInUser", admin))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/complaint-form"));

        mockMvc.perform(post("/admin/complaints/{id}/edit", complaint.getId())
                        .sessionAttr("loggedInUser", admin)
                        .param("title", "Updated web complaint")
                        .param("description", "Updated from admin form.")
                        .param("category", "Updated")
                        .param("complaintDate", "2026-06-15")
                        .param("status", "PENDING")
                        .param("comments", "Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/complaints"));

        mockMvc.perform(post("/admin/complaints/{id}/status", complaint.getId())
                        .sessionAttr("loggedInUser", admin)
                        .param("status", "COMPLETED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/complaints"));

        Complaint deleteCandidate = complaintRepository.save(complaint("Delete candidate", customer));
        mockMvc.perform(post("/admin/complaints/{id}/delete", deleteCandidate.getId())
                        .sessionAttr("loggedInUser", admin))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/complaints"));
    }

    @Test
    void assignmentAndEmployeeScreensWork() throws Exception {
        mockMvc.perform(get("/admin/assignments").sessionAttr("loggedInUser", admin))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/assignments"));

        mockMvc.perform(post("/admin/assignments")
                        .sessionAttr("loggedInUser", admin)
                        .param("complaintId", String.valueOf(complaint.getId()))
                        .param("employeeId", String.valueOf(employee.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/assignments"));

        mockMvc.perform(get("/employee/complaints").sessionAttr("loggedInUser", employee))
                .andExpect(status().isOk())
                .andExpect(view().name("employee/complaints"));

        mockMvc.perform(post("/employee/complaints/{id}/update", complaint.getId())
                        .sessionAttr("loggedInUser", employee)
                        .param("status", "COMPLETED")
                        .param("employeeRemarks", "Completed from web"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employee/complaints"));
    }

    @Test
    void customerScreensAndActionsWork() throws Exception {
        mockMvc.perform(get("/user/complaints").sessionAttr("loggedInUser", customer))
                .andExpect(status().isOk())
                .andExpect(view().name("user/complaints"));

        mockMvc.perform(get("/user/complaints/new").sessionAttr("loggedInUser", customer))
                .andExpect(status().isOk())
                .andExpect(view().name("user/complaint-form"));

        mockMvc.perform(post("/user/complaints")
                        .sessionAttr("loggedInUser", customer)
                        .param("title", "Customer form complaint")
                        .param("description", "Customer created complaint from form.")
                        .param("category", "Customer")
                        .param("complaintDate", "2026-06-15")
                        .param("comments", "Customer comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/complaints"));

        mockMvc.perform(post("/user/complaints/{id}/comments", complaint.getId())
                        .sessionAttr("loggedInUser", customer)
                        .param("comments", "Updated customer comment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/complaints"));

        mockMvc.perform(post("/user/complaints/{id}/feedback", complaint.getId())
                        .sessionAttr("loggedInUser", customer)
                        .param("feedback", "Helpful response"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/complaints"));
    }

    @Test
    void reportScreensWork() throws Exception {
        mockMvc.perform(get("/reports").sessionAttr("loggedInUser", admin))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/index"));

        mockMvc.perform(get("/reports/status")
                        .sessionAttr("loggedInUser", admin)
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/index"));

        mockMvc.perform(get("/reports/date-range")
                        .sessionAttr("loggedInUser", admin)
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-12-31"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/index"));

        mockMvc.perform(get("/reports/user")
                        .sessionAttr("loggedInUser", admin)
                        .param("userId", String.valueOf(customer.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/index"));
    }

    @Test
    void protectedWebRoutesRedirectAnonymousUsers() throws Exception {
        mockMvc.perform(get("/admin/complaints"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/admin/assignments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/employee/complaints"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/user/complaints"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/reports"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    private User user(String name, String email, Role role, boolean available) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("password123");
        user.setPhone("1234567890");
        user.setRole(role);
        user.setAvailable(available);
        return user;
    }

    private Complaint complaint(String title, User customer) {
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription("Complaint for delete flow.");
        complaint.setCategory("General");
        complaint.setComplaintDate(LocalDate.of(2026, 6, 15));
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setCustomer(customer);
        return complaint;
    }
}
