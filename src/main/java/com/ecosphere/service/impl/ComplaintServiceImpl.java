package com.ecosphere.service.impl;

import com.ecosphere.dto.UpvoteResponse;
import com.ecosphere.dto.UserResponse;
import com.ecosphere.entity.*;
import com.ecosphere.repository.ComplaintRepository;
import com.ecosphere.repository.UpvoteRepository;
import com.ecosphere.repository.UserRepository;
import com.ecosphere.service.CloudinaryService;
import com.ecosphere.service.ComplaintService;
import com.ecosphere.dto.CreateComplaintRequest;
import org.springframework.web.multipart.MultipartFile;
import com.ecosphere.entity.Upvote;
import com.ecosphere.dto.ComplaintResponse;
import com.ecosphere.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    private final UpvoteRepository upvoteRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public ComplaintServiceImpl(
            ComplaintRepository complaintRepository,
            UpvoteRepository upvoteRepository,
            UserRepository userRepository,
            CloudinaryService cloudinaryService
    ){
        this.complaintRepository = complaintRepository;
        this.upvoteRepository = upvoteRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }




    @Override
    public ComplaintResponse createComplaint(
            CreateComplaintRequest request,
            MultipartFile image
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Complaint complaint = new Complaint();

        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());

        complaint.setLatitude(request.getLatitude());
        complaint.setLongitude(request.getLongitude());

        complaint.setUser(user);

        complaint.setCreatedAt(LocalDateTime.now());

        complaint.setStatus(ComplaintStatus.PENDING);
        if(image != null && !image.isEmpty()) {

            String imageUrl =
                    cloudinaryService.uploadFile(image);

            complaint.setImageUrl(imageUrl);
        }

        Complaint savedComplaint =
                complaintRepository.save(complaint);

        return convertToResponse(savedComplaint);
    }
    @Override
    public List<ComplaintResponse> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository
                .findByStatus(status)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    @Override
    public List<ComplaintResponse> getAllComplaints() {

        return complaintRepository
                .findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    @Override
    public ComplaintResponse updateComplaintStatus(Long id, ComplaintStatus status) {

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        complaint.setStatus(status);

        Complaint updatedComplaint =
                complaintRepository.save(complaint);

        return convertToResponse(updatedComplaint);
    }
    @Override
    public List<ComplaintResponse> getComplaintsByCategory(String category) {
        return complaintRepository
                .findByCategory(category)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<ComplaintResponse> getComplaintsBySeverity(SeverityLevel severity) {
        return complaintRepository
                .findBySeverity(severity)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    @Override
    public UpvoteResponse addUpvote(Long complaintId, String email) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyUpvoted =
                upvoteRepository.findByUserAndComplaint(user, complaint)
                        .isPresent();

        if(alreadyUpvoted) {
            throw new RuntimeException("Already upvoted");
        }

        Upvote upvote = Upvote.builder()
                .user(user)
                .complaint(complaint)
                .build();

        upvoteRepository.save(upvote);

        complaint.setUpvoteCount(
                complaint.getUpvoteCount() + 1
        );

        complaintRepository.save(complaint);

        return new UpvoteResponse(
                complaint.getId(),
                complaint.getUpvoteCount()
        );
    }
    @Override
    public List<ComplaintResponse> getMyComplaints() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return complaintRepository
                .findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    @Override
    public UpvoteResponse removeUpvote(Long complaintId, String email) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Upvote upvote = upvoteRepository
                .findByUserAndComplaint(user, complaint)
                .orElseThrow(() ->
                        new RuntimeException("Upvote not found"));

        upvoteRepository.delete(upvote);

        complaint.setUpvoteCount(
                complaint.getUpvoteCount() - 1
        );

        complaintRepository.save(complaint);

        return new UpvoteResponse(
                complaint.getId(),
                complaint.getUpvoteCount()
        );
    }
    private ComplaintResponse convertToResponse(
            Complaint complaint
    ) {

        return ComplaintResponse.builder()
                .id(complaint.getId())
                .title(complaint.getTitle())
                .description(complaint.getDescription())
                .category(complaint.getCategory())
                .severity(
                        complaint.getSeverity() != null
                                ? complaint.getSeverity().name()
                                : null
                )
                .status(
                        complaint.getStatus() != null
                                ? complaint.getStatus().name()
                                : null
                )
                .latitude(complaint.getLatitude())
                .longitude(complaint.getLongitude())
                .imageUrl(complaint.getImageUrl())
                .upvoteCount(complaint.getUpvoteCount())
                .createdAt(complaint.getCreatedAt())

                .user(
                        UserResponse.builder()
                                .id(complaint.getUser().getId())
                                .name(complaint.getUser().getName())
                                .email(complaint.getUser().getEmail())
                                .role(complaint.getUser().getRole().name())
                                .build()
                )
                .build();
    }

}