package com.ecosphere.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ComplaintResponse {

    private Long id;

    private String title;

    private String description;

    private String category;

    private String severity;

    private String status;

    private Double latitude;

    private Double longitude;

    private String imageUrl;

    private Integer upvoteCount;
    private Integer priorityScore;

    private LocalDateTime createdAt;

    private UserResponse user;
}