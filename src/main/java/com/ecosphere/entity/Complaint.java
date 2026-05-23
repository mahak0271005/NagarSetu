package com.ecosphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
    private SeverityLevel severity;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    private Double latitude;

    private Double longitude;

    private String imageUrl;

    private Integer upvoteCount = 0;

    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void setCreatedAt(LocalDateTime now) {
    }

    public void setStatus(ComplaintStatus status) {
    }
}