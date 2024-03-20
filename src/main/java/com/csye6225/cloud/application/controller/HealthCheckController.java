package com.csye6225.cloud.application.controller;

import com.csye6225.cloud.application.service.HealthCheckService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

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
            logger.error("Invalid request method");
            return ResponseEntity.status(405).headers(httpHeaders).build();
        }
        if(request.getContentType() != null && request.getContentType().toLowerCase().contains("multipart/form-data")) {
            logger.error("Invalid request content type");
            return ResponseEntity.status(400).headers(httpHeaders).build();
        }
        if(request.getInputStream().read() != -1){
            logger.error("Invalid request input stream");
            return ResponseEntity.status(400).headers(httpHeaders).build();
        }
        if(! request.getParameterMap().isEmpty()) {
            logger.error("Invalid request parameter map");
            return ResponseEntity.status(400).headers(httpHeaders).build();
        }
        if(healthCheckService.getDBHealth()) {
            logger.info("Health check passed");
            return ResponseEntity.ok().headers(httpHeaders).build();
        } else {
            logger.error("Health check failed");
            return ResponseEntity.status(503).headers(httpHeaders).build();
        }
    }
}
