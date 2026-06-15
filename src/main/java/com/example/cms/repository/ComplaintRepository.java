package com.example.cms.repository;

import com.example.cms.model.Complaint;
import com.example.cms.model.ComplaintStatus;
import com.example.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByComplaintDateBetween(LocalDate startDate, LocalDate endDate);

    List<Complaint> findByCustomer(User customer);

    List<Complaint> findByAssignedEmployee(User employee);
}
