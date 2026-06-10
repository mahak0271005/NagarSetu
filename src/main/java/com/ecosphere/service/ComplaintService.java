package com.ecosphere.service;

import com.ecosphere.dto.ComplaintResponse;
import com.ecosphere.dto.UpvoteResponse;
import com.ecosphere.entity.Complaint;
import org.springframework.web.multipart.MultipartFile;
import com.ecosphere.dto.CreateComplaintRequest;
import com.ecosphere.entity.ComplaintStatus;
import com.ecosphere.entity.SeverityLevel;

import java.util.List;

public interface ComplaintService {

    ComplaintResponse createComplaint(
            CreateComplaintRequest request,
            MultipartFile image

    );
    List<ComplaintResponse> getAllComplaints();

    ComplaintResponse updateComplaintStatus(Long id, ComplaintStatus status);

    List<ComplaintResponse> getComplaintsByStatus(ComplaintStatus status);

    List<ComplaintResponse> getComplaintsByCategory(String category);

    List<ComplaintResponse> getComplaintsBySeverity(SeverityLevel severity);
    UpvoteResponse addUpvote(Long id, String email);
    UpvoteResponse removeUpvote(Long complaintId, String email);
    List<ComplaintResponse> getMyComplaints();
}