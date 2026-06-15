package com.example.cms.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ModelTest {

    @Test
    void userGettersAndSettersWork() {
        User user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("name@cms.com");
        user.setPassword("password");
        user.setPhone("1234567890");
        user.setRole(Role.ADMIN);
        user.setAvailable(false);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Name");
        assertThat(user.getEmail()).isEqualTo("name@cms.com");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getPhone()).isEqualTo("1234567890");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.isAvailable()).isFalse();
    }

    @Test
    void complaintGettersAndSettersWork() {
        User customer = new User();
        User employee = new User();
        LocalDate date = LocalDate.of(2026, 6, 15);
        Complaint complaint = new Complaint();
        complaint.setId(2L);
        complaint.setTitle("Title");
        complaint.setDescription("Description");
        complaint.setCategory("Category");
        complaint.setComplaintDate(date);
        complaint.setStatus(ComplaintStatus.COMPLETED);
        complaint.setComments("Comments");
        complaint.setEmployeeRemarks("Remarks");
        complaint.setFeedback("Feedback");
        complaint.setCustomer(customer);
        complaint.setAssignedEmployee(employee);

        assertThat(complaint.getId()).isEqualTo(2L);
        assertThat(complaint.getTitle()).isEqualTo("Title");
        assertThat(complaint.getDescription()).isEqualTo("Description");
        assertThat(complaint.getCategory()).isEqualTo("Category");
        assertThat(complaint.getComplaintDate()).isEqualTo(date);
        assertThat(complaint.getStatus()).isEqualTo(ComplaintStatus.COMPLETED);
        assertThat(complaint.getComments()).isEqualTo("Comments");
        assertThat(complaint.getEmployeeRemarks()).isEqualTo("Remarks");
        assertThat(complaint.getFeedback()).isEqualTo("Feedback");
        assertThat(complaint.getCustomer()).isSameAs(customer);
        assertThat(complaint.getAssignedEmployee()).isSameAs(employee);
    }

    @Test
    void dateHelperProvidesDateParts() {
        Date date = new Date(LocalDate.of(2026, 6, 15));

        assertThat(date.getValue()).isEqualTo(LocalDate.of(2026, 6, 15));
        assertThat(date.getDay()).isEqualTo(15);
        assertThat(date.getMonth()).isEqualTo(6);
        assertThat(date.getYear()).isEqualTo(2026);
        assertThat(date.getFormattedDate()).isEqualTo("2026-06-15");
        assertThat(Date.today()).isNotNull();
        assertThat(Date.now()).isNotNull();
    }
}
