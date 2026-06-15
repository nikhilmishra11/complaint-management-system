package com.example.cms.service;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.repository.ComplaintRepository;
import com.example.cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssignationServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private UserRepository userRepository;

    private AssignationService assignationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assignationService = new AssignationService(complaintRepository, userRepository);
    }

    @Test
    void getAvailableReturnsAvailableEmployees() {
        User employee = user(Role.EMPLOYEE);
        when(userRepository.findByRoleAndAvailableTrue(Role.EMPLOYEE)).thenReturn(List.of(employee));

        assertThat(assignationService.getAvailable()).containsExactly(employee);
    }

    @Test
    void assignComplaintAssignsEmployeeAndMarksInProgress() {
        Complaint complaint = new Complaint();
        User employee = user(Role.EMPLOYEE);
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));
        when(userRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(complaintRepository.save(complaint)).thenReturn(complaint);

        Complaint assigned = assignationService.assignComplaint(1L, 2L);

        assertThat(assigned.getAssignedEmployee()).isSameAs(employee);
        assertThat(assigned.getStatus()).isEqualTo(ComplaintStatus.IN_PROGRESS);
        assertThat(employee.isAvailable()).isFalse();
        verify(userRepository).save(employee);
        verify(complaintRepository).save(complaint);
    }

    @Test
    void assignComplaintRejectsMissingOrWrongRole() {
        Complaint complaint = new Complaint();
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));
        when(complaintRepository.findById(9L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        when(userRepository.findById(3L)).thenReturn(Optional.of(user(Role.CUSTOMER)));

        assertThatThrownBy(() -> assignationService.assignComplaint(9L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Complaint not found");
        assertThatThrownBy(() -> assignationService.assignComplaint(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Employee not found");
        assertThatThrownBy(() -> assignationService.assignComplaint(1L, 3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Selected user is not an employee");
    }

    private User user(Role role) {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@cms.com");
        user.setPassword("password");
        user.setPhone("1234567890");
        user.setRole(role);
        user.setAvailable(true);
        return user;
    }
}
