package com.example.cms.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class CmsApiStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MvcResult lastResult;
    private long customerId = 2L;
    private long employeeId = 4L;
    private long complaintId;

    @Given("the CMS API is running")
    public void theCmsApiIsRunning() throws Exception {
        lastResult = mockMvc.perform(get("/api/complaints")).andReturn();
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(200);
    }

    @When("I login as the seeded admin")
    public void iLoginAsTheSeededAdmin() throws Exception {
        lastResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@cms.com","password":"admin123"}
                                """))
                .andReturn();
    }

    @When("I register a customer through the API")
    public void iRegisterACustomerThroughTheApi() throws Exception {
        lastResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Cucumber Customer",
                                  "email":"cucumber.customer@cms.com",
                                  "password":"secret123",
                                  "phone":"9876543210",
                                  "role":"CUSTOMER"
                                }
                                """))
                .andReturn();
        if (lastResult.getResponse().getStatus() == 201) {
            customerId = json().get("id").asLong();
        }
    }

    @When("I reset the seeded customer password")
    public void iResetTheSeededCustomerPassword() throws Exception {
        lastResult = mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"customer@cms.com","password":"customer123"}
                                """))
                .andReturn();
    }

    @When("I create a complaint for that customer")
    public void iCreateAComplaintForThatCustomer() throws Exception {
        lastResult = mockMvc.perform(post("/api/complaints")
                        .param("customerId", String.valueOf(customerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"Cucumber complaint",
                                  "description":"Complaint created by Cucumber end to end test.",
                                  "category":"General",
                                  "complaintDate":"2026-06-15",
                                  "status":"PENDING",
                                  "comments":"Initial comment"
                                }
                                """))
                .andReturn();
        complaintId = json().get("id").asLong();
    }

    @When("I update the complaint details")
    public void iUpdateTheComplaintDetails() throws Exception {
        lastResult = mockMvc.perform(put("/api/complaints/{id}", complaintId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title":"Updated cucumber complaint",
                                  "description":"Updated by Cucumber test.",
                                  "category":"Updated",
                                  "complaintDate":"2026-06-15",
                                  "status":"PENDING",
                                  "comments":"Updated comment"
                                }
                                """))
                .andReturn();
    }

    @When("I change the complaint status to in progress")
    public void iChangeTheComplaintStatusToInProgress() throws Exception {
        lastResult = mockMvc.perform(patch("/api/complaints/{id}/status", complaintId)
                        .param("status", "IN_PROGRESS"))
                .andReturn();
    }

    @When("I add customer comments and feedback")
    public void iAddCustomerCommentsAndFeedback() throws Exception {
        lastResult = mockMvc.perform(patch("/api/complaints/{id}/comments", complaintId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comments\":\"Cucumber customer comment\"}"))
                .andReturn();
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(200);

        lastResult = mockMvc.perform(patch("/api/complaints/{id}/feedback", complaintId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"feedback\":\"Cucumber feedback\"}"))
                .andReturn();
    }

    @When("I assign the complaint to an employee")
    public void iAssignTheComplaintToAnEmployee() throws Exception {
        lastResult = mockMvc.perform(post("/api/assignments")
                        .param("complaintId", String.valueOf(complaintId))
                        .param("employeeId", String.valueOf(employeeId)))
                .andReturn();
    }

    @When("the employee completes the complaint with remarks")
    public void theEmployeeCompletesTheComplaintWithRemarks() throws Exception {
        lastResult = mockMvc.perform(patch("/api/employees/complaints/{id}", complaintId)
                        .param("status", "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeRemarks\":\"Cucumber completed\"}"))
                .andReturn();
    }

    @Then("the latest response status is {int}")
    public void theLatestResponseStatusIs(int status) {
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(status);
    }

    @Then("the complaint can be viewed by id")
    public void theComplaintCanBeViewedById() throws Exception {
        lastResult = mockMvc.perform(get("/api/complaints/{id}", complaintId)).andReturn();
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(json().get("id").asLong()).isEqualTo(complaintId);
    }

    @Then("the customer can view the complaint")
    public void theCustomerCanViewTheComplaint() throws Exception {
        lastResult = mockMvc.perform(get("/api/users/{userId}/complaints", customerId)).andReturn();
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(lastResult.getResponse().getContentAsString()).contains(String.valueOf(complaintId));
    }

    @Then("the employee can view the assigned complaint")
    public void theEmployeeCanViewTheAssignedComplaint() throws Exception {
        lastResult = mockMvc.perform(get("/api/employees/{employeeId}/complaints", employeeId)).andReturn();
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(200);
        assertThat(lastResult.getResponse().getContentAsString()).contains(String.valueOf(complaintId));
    }

    @Then("pending completed date range and user reports are available")
    public void reportsAreAvailable() throws Exception {
        assertOk("/api/reports/status?status=PENDING");
        assertOk("/api/reports/status?status=COMPLETED");
        assertOk("/api/reports/date-range?startDate=2026-01-01&endDate=2026-12-31");
        assertOk("/api/reports/user?userId=" + customerId);
    }

    @Then("I can delete the complaint")
    public void iCanDeleteTheComplaint() throws Exception {
        lastResult = mockMvc.perform(delete("/api/complaints/{id}", complaintId)).andReturn();
        assertThat(lastResult.getResponse().getStatus()).isEqualTo(204);
    }

    private void assertOk(String url) throws Exception {
        MvcResult result = mockMvc.perform(get(url)).andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    private JsonNode json() throws Exception {
        return objectMapper.readTree(lastResult.getResponse().getContentAsString());
    }
}
