package com.example.cms.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authEndpointsWork() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@cms.com","password":"admin123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Endpoint Customer",
                                  "email":"endpoint.customer@cms.com",
                                  "password":"secret123",
                                  "phone":"9876543210",
                                  "role":"CUSTOMER"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CUSTOMER"));

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"endpoint.customer@cms.com","password":"newsecret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successful"));
    }

    @Test
    void complaintAssignmentAndReportEndpointsWork() throws Exception {
        String createResponse = mockMvc.perform(post("/api/complaints")
                        .param("customerId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"Endpoint complaint",
                                  "description":"Created by MockMvc endpoint test.",
                                  "category":"General",
                                  "complaintDate":"2026-06-15",
                                  "status":"PENDING",
                                  "comments":"Initial"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String complaintId = createResponse.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/complaints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(get("/api/complaints/{id}", complaintId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Endpoint complaint"));

        mockMvc.perform(put("/api/complaints/{id}", complaintId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"Endpoint complaint updated",
                                  "description":"Updated by MockMvc endpoint test.",
                                  "category":"Updated",
                                  "complaintDate":"2026-06-15",
                                  "status":"PENDING",
                                  "comments":"Updated"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Endpoint complaint updated"));

        mockMvc.perform(patch("/api/complaints/{id}/status", complaintId)
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(patch("/api/complaints/{id}/comments", complaintId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comments\":\"Customer comment\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").value("Customer comment"));

        mockMvc.perform(get("/api/assignments/available-employees"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/assignments")
                        .param("complaintId", complaintId)
                        .param("employeeId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignedEmployee.id").value(3));

        mockMvc.perform(get("/api/employees/{employeeId}/complaints", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        mockMvc.perform(patch("/api/employees/complaints/{id}", complaintId)
                        .param("status", "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeRemarks\":\"Completed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.employeeRemarks").value("Completed"));

        mockMvc.perform(patch("/api/complaints/{id}/feedback", complaintId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"feedback\":\"Good service\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedback").value("Good service"));

        mockMvc.perform(get("/api/users/{userId}/complaints", 2))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reports/status").param("status", "COMPLETED"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reports/date-range")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-12-31"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/reports/user").param("userId", "2"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/complaints/{id}", complaintId))
                .andExpect(status().isNoContent());
    }

    @Test
    void apiExceptionHandlerReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/complaints/{id}", 99999))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Complaint not found"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }
}
