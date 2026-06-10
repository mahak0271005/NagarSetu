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

import com.ecosphere.dto.ComplaintResponse;

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

private int calculatePriority(
        SeverityLevel severity,
        int upvotes
) {

    if(severity == null) {
        return upvotes;
    }

    int severityScore = switch (severity) {
        case LOW -> 1;
        case MODERATE -> 3;
        case HIGH -> 5;
        case CRITICAL -> 8;
    };

    return severityScore + upvotes;
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
        complaint.setSeverity(request.getSeverity());
        complaint.setCategory(
        autoTag(
                request.getTitle(),
                request.getDescription()
        )
);

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
        complaint.setPriorityScore(
        calculatePriority(
                complaint.getSeverity(),
                complaint.getUpvoteCount()
        )
);
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
        complaint.setPriorityScore(
        calculatePriority(
                complaint.getSeverity(),
                complaint.getUpvoteCount()
        )
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
        complaint.setPriorityScore(
        calculatePriority(
                complaint.getSeverity(),
                complaint.getUpvoteCount()
        )
);

        complaintRepository.save(complaint);

        return new UpvoteResponse(
                complaint.getId(),
                complaint.getUpvoteCount()
        );
    }
    private String autoTag(String title, String description) {

    String text = (title + " " + description).toLowerCase();

    // ROAD
    if(text.contains("road")
            || text.contains("pothole")
            || text.contains("gaddha")
            || text.contains("street")
            || text.contains("broken road")
            || text.contains("damaged road")
            || text.contains("crack")) {
        return "ROAD";
    }

    // WATER
    if(text.contains("water")
            || text.contains("pani")
            || text.contains("pipeline")
            || text.contains("leakage")
            || text.contains("water supply")
            || text.contains("drain")
            || text.contains("sewer")
            || text.contains("nala")
            || text.contains("drainage")) {
        return "WATER";
    }

    // ELECTRICITY
    if(text.contains("electricity")
            || text.contains("power")
            || text.contains("light")
            || text.contains("street light")
            || text.contains("transformer")
            || text.contains("wire")
            || text.contains("electric pole")
            || text.contains("power cut")
            || text.contains("blackout")) {
        return "ELECTRICITY";
    }

    // SANITATION
    if(text.contains("garbage")
            || text.contains("kachra")
            || text.contains("waste")
            || text.contains("dustbin")
            || text.contains("cleanliness")
            || text.contains("dirty")
            || text.contains("overflowing garbage")) {
        return "SANITATION";
    }

    // TRAFFIC
    if(text.contains("traffic")
            || text.contains("signal")
            || text.contains("jam")
            || text.contains("crossing")
            || text.contains("accident")
            || text.contains("parking")) {
        return "TRAFFIC";
    }

    // PUBLIC SAFETY
    if(text.contains("crime")
            || text.contains("theft")
            || text.contains("robbery")
            || text.contains("unsafe")
            || text.contains("security")
            || text.contains("harassment")) {
        return "PUBLIC_SAFETY";
    }

    // ANIMAL
    if(text.contains("dog")
            || text.contains("stray dog")
            || text.contains("cow")
            || text.contains("animal")) {
        return "ANIMAL_CONTROL";
    }

    // PARKS
    if(text.contains("park")
            || text.contains("garden")
            || text.contains("playground")
            || text.contains("tree")) {
        return "PARKS_AND_GREENERY";
    }

    return "OTHER";
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
.priorityScore(complaint.getPriorityScore())
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