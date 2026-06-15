package com.example.cms.service;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.User;
import com.example.cms.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Transactional
    public Complaint addComplaint(Complaint complaint) {
        try {
            if (complaint.getComplaintDate() == null) {
                complaint.setComplaintDate(LocalDate.now());
            }
            if (complaint.getStatus() == null) {
                complaint.setStatus(ComplaintStatus.PENDING);
            }
            return complaintRepository.save(complaint);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to add complaint", ex);
        }
    }

    @Transactional
    public Complaint updateComplaint(Long id, Complaint updatedComplaint) {
        try {
            Complaint complaint = getComplaint(id);
            complaint.setTitle(updatedComplaint.getTitle());
            complaint.setDescription(updatedComplaint.getDescription());
            complaint.setCategory(updatedComplaint.getCategory());
            complaint.setComplaintDate(updatedComplaint.getComplaintDate());
            complaint.setStatus(updatedComplaint.getStatus());
            complaint.setComments(updatedComplaint.getComments());
            complaint.setEmployeeRemarks(updatedComplaint.getEmployeeRemarks());
            complaint.setFeedback(updatedComplaint.getFeedback());
            return complaintRepository.save(complaint);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to update complaint", ex);
        }
    }

    @Transactional
    public void deleteComplaint(Long id) {
        try {
            if (!complaintRepository.existsById(id)) {
                throw new IllegalArgumentException("Complaint not found");
            }
            complaintRepository.deleteById(id);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to delete complaint", ex);
        }
    }

    public List<Complaint> loadComplaints() {
        try {
            return complaintRepository.findAll();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load complaints", ex);
        }
    }

    public Complaint getComplaint(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
    }

    public List<Complaint> findByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status);
    }

    public List<Complaint> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return complaintRepository.findByComplaintDateBetween(startDate, endDate);
    }

    public List<Complaint> findByCustomer(User customer) {
        return complaintRepository.findByCustomer(customer);
    }

    public List<Complaint> findByAssignedEmployee(User employee) {
        return complaintRepository.findByAssignedEmployee(employee);
    }
}
