package com.ecosphere.service.impl;

import com.ecosphere.entity.Complaint;
import com.ecosphere.entity.ComplaintStatus;
import com.ecosphere.repository.ComplaintRepository;
import com.ecosphere.service.ComplaintService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    @Override
    public Complaint createComplaint(Complaint complaint) {

        complaint.setCreatedAt(LocalDateTime.now());

        return complaintRepository.save(complaint);
    }
    @Override
    public List<Complaint> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status);
    }
    @Override
    public List<Complaint> getAllComplaints() {

        return complaintRepository.findAll();
    }
    @Override
    public Complaint updateComplaintStatus(Long id, ComplaintStatus status) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(status);

        return complaintRepository.save(complaint);
    }
}