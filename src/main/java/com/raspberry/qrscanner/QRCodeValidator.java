package com.raspberry.qrscanner;

import nu.pattern.OpenCV;
import org.springframework.web.client.RestTemplate;

public class QRCodeValidator {
    static {
        System.loadLibrary("opencv_java451"); // Załaduj bibliotekę OpenCV 4.5.5
    }
    private final RestTemplate restTemplate;
    private final String validationServerUrl;

    public QRCodeValidator() {
        this.restTemplate = new RestTemplate();
        this.validationServerUrl = "http:192.168.66.0:8080/api/digital-key/validate";
    }

    public boolean validateQRCode(String qrCode) {
        return restTemplate.postForObject(validationServerUrl, qrCode, Boolean.class);
    }
}
