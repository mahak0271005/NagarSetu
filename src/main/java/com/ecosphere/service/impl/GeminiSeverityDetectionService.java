package com.ecosphere.service.impl;
import com.ecosphere.entity.SeverityLevel;
import com.ecosphere.service.SeverityDetectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GeminiSeverityDetectionService
        implements SeverityDetectionService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    public GeminiSeverityDetectionService(
            RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    @Override
    public SeverityLevel detectSeverity(
            MultipartFile image
    ) {

        System.out.println(
                "AI Severity Service Called"
        );

        return SeverityLevel.MODERATE;
    }
}
