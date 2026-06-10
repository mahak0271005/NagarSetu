package com.ecosphere.dto;

import lombok.Data;

@Data
public class CreateComplaintRequest {

    private String title;

    private String description;

    private String category;

    private Double latitude;

    private Double longitude;
}