package com.ecosphere.repository;

import com.ecosphere.entity.Complaint;
import com.ecosphere.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository
        extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStatus(ComplaintStatus status);
}