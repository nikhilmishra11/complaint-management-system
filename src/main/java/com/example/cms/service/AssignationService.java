package com.example.cms.service;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.Role;
import com.example.cms.model.User;
import com.example.cms.repository.ComplaintRepository;
import com.example.cms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssignationService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public AssignationService(ComplaintRepository complaintRepository, UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    public List<User> getAvailable() {
        try {
            return userRepository.findByRoleAndAvailableTrue(Role.EMPLOYEE);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load available employees", ex);
        }
    }

    @Transactional
    public Complaint assignComplaint(Long complaintId, Long employeeId) {
        try {
            Complaint complaint = complaintRepository.findById(complaintId)
                    .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
            User employee = userRepository.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
            if (employee.getRole() != Role.EMPLOYEE) {
                throw new IllegalArgumentException("Selected user is not an employee");
            }
            complaint.setAssignedEmployee(employee);
            complaint.setStatus(ComplaintStatus.IN_PROGRESS);
            employee.setAvailable(false);
            userRepository.save(employee);
            return complaintRepository.save(complaint);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to assign complaint", ex);
        }
    }
}
