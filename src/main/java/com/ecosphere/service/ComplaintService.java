package com.ecosphere.service;

import com.ecosphere.entity.Complaint;
import com.ecosphere.entity.ComplaintStatus;

import java.util.List;

public interface ComplaintService {

    Complaint createComplaint(Complaint complaint);
    List<Complaint> getAllComplaints();
    Complaint updateComplaintStatus(Long id, ComplaintStatus status);
    List<Complaint> getComplaintsByStatus(ComplaintStatus status);

}