package com.csye6225.cloud.application.controller;

import com.csye6225.cloud.application.dto.UserResponseDTO;
import com.csye6225.cloud.application.entity.User;
import com.csye6225.cloud.application.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1")
@CrossOrigin
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);

    private final UserService userService;

    HttpHeaders httpHeaders = new HttpHeaders();

    public UserRestController(UserService userService) {
        this.userService = userService;
        httpHeaders.setPragma("no-cache");
        httpHeaders.add("X-Content-Type-Options", "nosniff");
        httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
    }

    @PostMapping("/user")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody User payload) {
        logger.info("Creating user");
        User user = userService.createUser(payload);
        UserResponseDTO userResponseDTO = new UserResponseDTO(user);
        logger.info("User created");
        return ResponseEntity.status(201).headers(httpHeaders).body(userResponseDTO);
    }

    @GetMapping("/user/self")
    public ResponseEntity<UserResponseDTO> getUser(Authentication authenication, HttpServletRequest request) throws IOException {
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
        User user = userService.findByUsername(authenication.getName());
        UserResponseDTO userResponseDTO = new UserResponseDTO(user);
        return ResponseEntity.ok().headers(httpHeaders).body(userResponseDTO);
    }

    @PutMapping("/user/self")
    public ResponseEntity<Void> updateUser(Authentication authenication, @RequestBody User payload) {
        User user = userService.updateUser(authenication.getName(), payload);
        return ResponseEntity.status(204).headers(httpHeaders).build();
    }

}
