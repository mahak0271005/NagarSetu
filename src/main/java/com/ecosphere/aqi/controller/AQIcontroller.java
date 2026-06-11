package com.ecosphere.aqi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ecosphere.aqi.model.AQIresponse;
import com.ecosphere.aqi.service.AQIservice;

@RestController
@RequestMapping("/api/aqi")
@CrossOrigin
public class AQIcontroller {

    @Autowired
    private AQIservice service;

    @GetMapping
    public AQIresponse getAQI(
            @RequestParam double lat,
            @RequestParam double lon) {

        return service.getAQI(lat, lon);
    }
}

