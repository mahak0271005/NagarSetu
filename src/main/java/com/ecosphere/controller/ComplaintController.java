package com.ecosphere.controller;

import com.ecosphere.entity.Complaint;
import com.ecosphere.entity.ComplaintStatus;
import com.ecosphere.service.ComplaintService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    public Complaint createComplaint(@RequestBody Complaint complaint) {

        return complaintService.createComplaint(complaint);
    }
    @GetMapping
    public List<Complaint> getAllComplaints() {

        return complaintService.getAllComplaints();
    }
    @PutMapping("/{id}/status")
    public Complaint updateComplaintStatus(
            @PathVariable Long id,
            @RequestParam ComplaintStatus status
    ) {
        return complaintService.updateComplaintStatus(id, status);
    }
    @GetMapping("/status")
    public List<Complaint> getComplaintsByStatus(
            @RequestParam ComplaintStatus status
    ) {
        return complaintService.getComplaintsByStatus(status);
    }
}