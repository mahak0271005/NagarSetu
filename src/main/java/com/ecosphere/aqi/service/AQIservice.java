package com.ecosphere.aqi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import com.ecosphere.aqi.model.AQIresponse;

@Service
public class AQIservice {

    private final String API_KEY = "a126c81e144393d537b5d484a4443c28"; // 🔥 replace this

    public AQIresponse getAQI(double lat, double lon) {
        

        String url = "https://api.openweathermap.org/data/2.5/air_pollution?lat="
                + lat + "&lon=" + lon + "&appid=" + API_KEY;

        RestTemplate restTemplate = new RestTemplate();
        Map response = restTemplate.getForObject(url, Map.class);

        List list = (List) response.get("list");
        Map main = (Map) ((Map) list.get(0)).get("main");

        int aqi = (int) main.get("aqi");

        AQIresponse res = new AQIresponse();
        res.setAqi(aqi);
        res.setCategory(getCategory(aqi));

        return res;
    }

    private String getCategory(int aqi) {
        switch (aqi) {
            case 1: return "Good";
            case 2: return "Fair";
            case 3: return "Moderate";
            case 4: return "Poor";
            case 5: return "Very Poor";
            default: return "Unknown";
        }
    }
}
