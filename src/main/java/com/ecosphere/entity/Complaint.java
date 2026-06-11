package com.ecosphere.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {
    @JsonIgnore
    @OneToMany(mappedBy = "complaint")
    private List<Upvote> upvotes;

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
    private Integer priorityScore = 0;

    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    public void setCreatedAt(LocalDateTime now) {
        this.createdAt = now;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

}