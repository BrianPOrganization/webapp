package com.csye6225.cloud.application.controller;

import com.csye6225.cloud.application.dto.UserResponseDTO;
import com.csye6225.cloud.application.entity.User;
import com.csye6225.cloud.application.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@CrossOrigin
public class UserRestController {

    private final UserService userService;

    HttpHeaders httpHeaders = new HttpHeaders();

    public UserRestController(UserService userService) {
        this.userService = userService;
        httpHeaders.setPragma("no-cache");
        httpHeaders.add("X-Content-Type-Options", "nosniff");
        httpHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
    }

    @PostMapping("/user")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody User payload) throws NoSuchFieldException {
        User user = userService.createUser(payload);
        UserResponseDTO userResponseDTO = new UserResponseDTO(user);
        return ResponseEntity.ok().headers(httpHeaders).body(userResponseDTO);
    }

    @GetMapping("/user/self")
    public ResponseEntity<UserResponseDTO> getUser(Authentication authenication) {
        User user = userService.findByUsername(authenication.getName());
        UserResponseDTO userResponseDTO = new UserResponseDTO(user);
        return ResponseEntity.ok().headers(httpHeaders).body(userResponseDTO);
    }

    @PutMapping("/user/self")
    public ResponseEntity<UserResponseDTO> updateUser(Authentication authenication, @RequestBody User payload) {
        User user = userService.updateUser(authenication.getName(), payload);
        UserResponseDTO userResponseDTO = new UserResponseDTO(user);
        return ResponseEntity.ok().headers(httpHeaders).body(userResponseDTO);
    }

}
