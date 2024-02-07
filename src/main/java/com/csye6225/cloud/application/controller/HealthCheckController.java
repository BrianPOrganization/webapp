package com.csye6225.cloud.application.controller;

import com.csye6225.cloud.application.service.HealthCheckService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@CrossOrigin
public class HealthCheckController {

    @Autowired
    HealthCheckService healthCheckService;

    HttpHeaders httpHeaders = new HttpHeaders();

    public HealthCheckController() {
        httpHeaders.setPragma("no-cache");
        httpHeaders.add("X-Content-Type-Options", "nosniff");
        httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
    }

    @RequestMapping("/healthz")
    public ResponseEntity<Void> healthCheck(HttpServletRequest request) throws IOException {
        if(! "GET".equalsIgnoreCase(request.getMethod())) {
            return ResponseEntity.status(405).headers(httpHeaders).build();
        }
        if(request.getContentType() != null && request.getContentType().toLowerCase().contains("multipart/form-data")) {
            return ResponseEntity.status(400).headers(httpHeaders).build();
        }
        if(request.getInputStream().read() != -1){
            return ResponseEntity.status(400).headers(httpHeaders).build();
        }
        if(! request.getParameterMap().isEmpty()) {
            return ResponseEntity.status(400).headers(httpHeaders).build();
        }
        if(healthCheckService.getDBHealth()) {
            return ResponseEntity.ok().headers(httpHeaders).build();
        } else {
            return ResponseEntity.status(503).headers(httpHeaders).build();
        }
    }
}
