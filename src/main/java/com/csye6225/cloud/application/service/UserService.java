package com.csye6225.cloud.application.service;

import com.csye6225.cloud.application.entity.User;

import java.util.Optional;

public interface UserService {
    public User createUser(User user) throws NoSuchFieldException;
    public User updateUser(String username, User user);
    public User findByUsername(String email);
}
