package com.ecosphere.controller;

import com.ecosphere.dto.ComplaintResponse;
import com.ecosphere.dto.CreateComplaintRequest;
import com.ecosphere.dto.UpvoteResponse;
import com.ecosphere.service.CloudinaryService;
import org.springframework.security.access.prepost.PreAuthorize;
import com.ecosphere.entity.Complaint;
import com.ecosphere.entity.ComplaintStatus;
import com.ecosphere.entity.SeverityLevel;
import com.ecosphere.service.ComplaintService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final CloudinaryService cloudinaryService;

    public ComplaintController(
            ComplaintService complaintService,
            CloudinaryService cloudinaryService
    ) {
        this.complaintService = complaintService;
        this.cloudinaryService = cloudinaryService;
    }
    @PostMapping("/upload-image")
    public String uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        return cloudinaryService.uploadFile(file);
    }

    @PostMapping("/upload")
    public ComplaintResponse createComplaint(
            @RequestPart("data")
            CreateComplaintRequest request,

            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {

        return complaintService.createComplaint(
                request,
                image
        );
    }


    @GetMapping
    public List<ComplaintResponse> getAllComplaints(){
        return complaintService.getAllComplaints();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ComplaintResponse updateComplaintStatus(
            @PathVariable Long id,
            @RequestParam ComplaintStatus status
    ) {
        return complaintService.updateComplaintStatus(id, status);
    }
    @GetMapping("/status")
    public List<ComplaintResponse> getComplaintsByStatus(
            @RequestParam ComplaintStatus status
    ) {
        return complaintService.getComplaintsByStatus(status);
    }
    @GetMapping("/category")
    public List<ComplaintResponse> getComplaintsByCategory(
            @RequestParam String category
    ) {
        return complaintService.getComplaintsByCategory(category);
    }

    @GetMapping("/severity")
    public List<ComplaintResponse> getComplaintsBySeverity(
            @RequestParam SeverityLevel severity
    ) {
        return complaintService.getComplaintsBySeverity(severity);
    }
    @PostMapping("/{id}/upvote")
    public UpvoteResponse addUpvote(
            @PathVariable Long id,
            @RequestParam String email
    ) {
        return complaintService.addUpvote(id, email);
    }
    @DeleteMapping("/{id}/upvote")
    public UpvoteResponse removeUpvote(
            @PathVariable Long id,
            @RequestParam String email
    ) {
        return complaintService.removeUpvote(id, email);
    }
    @GetMapping("/my")
    public List<ComplaintResponse> getMyComplaints() {

        return complaintService.getMyComplaints();
    }
}