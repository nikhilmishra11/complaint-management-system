package com.example.cms.service;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.User;
import com.example.cms.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    private ComplaintService complaintService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        complaintService = new ComplaintService(complaintRepository);
    }

    @Test
    void addComplaintDefaultsDateAndStatus() {
        Complaint complaint = complaint("Title one");
        complaint.setComplaintDate(null);
        complaint.setStatus(null);
        when(complaintRepository.save(complaint)).thenReturn(complaint);

        Complaint saved = complaintService.addComplaint(complaint);

        assertThat(saved.getComplaintDate()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(ComplaintStatus.PENDING);
        verify(complaintRepository).save(complaint);
    }

    @Test
    void updateComplaintCopiesEditableFields() {
        Complaint existing = complaint("Old title");
        Complaint updated = complaint("New title");
        updated.setDescription("Updated description");
        updated.setCategory("Updated category");
        updated.setStatus(ComplaintStatus.COMPLETED);
        updated.setComments("Updated comments");
        updated.setEmployeeRemarks("Done");
        updated.setFeedback("Good");
        when(complaintRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(complaintRepository.save(existing)).thenReturn(existing);

        Complaint result = complaintService.updateComplaint(1L, updated);

        assertThat(result.getTitle()).isEqualTo("New title");
        assertThat(result.getStatus()).isEqualTo(ComplaintStatus.COMPLETED);
        assertThat(result.getEmployeeRemarks()).isEqualTo("Done");
        assertThat(result.getFeedback()).isEqualTo("Good");
    }

    @Test
    void deleteComplaintDeletesOnlyExistingComplaint() {
        when(complaintRepository.existsById(1L)).thenReturn(true);
        when(complaintRepository.existsById(2L)).thenReturn(false);

        complaintService.deleteComplaint(1L);

        verify(complaintRepository).deleteById(1L);
        assertThatThrownBy(() -> complaintService.deleteComplaint(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Complaint not found");
    }

    @Test
    void loadAndFindMethodsDelegateToRepository() {
        Complaint complaint = complaint("Delegated");
        User user = new User();
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);
        when(complaintRepository.findAll()).thenReturn(List.of(complaint));
        when(complaintRepository.findByStatus(ComplaintStatus.PENDING)).thenReturn(List.of(complaint));
        when(complaintRepository.findByComplaintDateBetween(start, end)).thenReturn(List.of(complaint));
        when(complaintRepository.findByCustomer(user)).thenReturn(List.of(complaint));
        when(complaintRepository.findByAssignedEmployee(user)).thenReturn(List.of(complaint));

        assertThat(complaintService.loadComplaints()).containsExactly(complaint);
        assertThat(complaintService.findByStatus(ComplaintStatus.PENDING)).containsExactly(complaint);
        assertThat(complaintService.findByDateRange(start, end)).containsExactly(complaint);
        assertThat(complaintService.findByCustomer(user)).containsExactly(complaint);
        assertThat(complaintService.findByAssignedEmployee(user)).containsExactly(complaint);
    }

    @Test
    void getComplaintThrowsWhenMissing() {
        when(complaintRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> complaintService.getComplaint(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Complaint not found");
    }

    private Complaint complaint(String title) {
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription("Detailed complaint description");
        complaint.setCategory("General");
        complaint.setComplaintDate(LocalDate.of(2026, 6, 15));
        complaint.setStatus(ComplaintStatus.PENDING);
        return complaint;
    }
}
