package com.csye6225.cloud.application.controller;

import com.csye6225.cloud.application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/verify")
@CrossOrigin
public class EmailVerificationController {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class);

    private final UserService userService;

    HttpHeaders httpHeaders = new HttpHeaders();

    public EmailVerificationController(UserService userService) {
        this.userService = userService;
        httpHeaders.setPragma("no-cache");
        httpHeaders.add("X-Content-Type-Options", "nosniff");
        httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
    }

    @GetMapping("")
    public ResponseEntity<String> verifyToken(@RequestParam String token, HttpServletRequest request) {
        if(! "GET".equalsIgnoreCase(request.getMethod())) {
            logger.error("Invalid request method");
            return ResponseEntity.status(405).headers(httpHeaders).build();
        }
        if(request.getContentType() != null && request.getContentType().toLowerCase().contains("multipart/form-data")) {
            logger.error("Invalid request content type");
            return ResponseEntity.status(400).headers(httpHeaders).build();
        }
        logger.info("Verifying token");
        if (token == null) {
            logger.error("Invalid token");
            return ResponseEntity.status(400).headers(httpHeaders).body("Invalid token");
        }
        if (userService.verifyToken(token)) {
            logger.info("Token verified");
            return ResponseEntity.status(200).headers(httpHeaders).body("Token verified");
        } else {
            logger.error("Token verification failed");
            return ResponseEntity.status(400).headers(httpHeaders).body("Token verification failed");
        }
    }

}
