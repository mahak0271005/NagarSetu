package com.ecosphere.dto;
import com.ecosphere.entity.SeverityLevel;
import lombok.Data;

@Data
public class CreateComplaintRequest {

    private String title;

    private String description;
private SeverityLevel severity;
    private Double latitude;

    private Double longitude;
}