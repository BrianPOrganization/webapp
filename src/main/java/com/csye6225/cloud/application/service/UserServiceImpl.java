package com.csye6225.cloud.application.service;

import com.csye6225.cloud.application.entity.User;
import com.csye6225.cloud.application.exception.BadRequestException;
import com.csye6225.cloud.application.respository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        if(findByUsername(user.getUsername()) != null) {
            throw new BadRequestException("Username already exists");
        }
        performFieldChecks(user);
        if(! isValidEmail(user.getUsername())) {
            throw new BadRequestException("Email provided is invalid");
        }
        if(! notNull(user.getPassword())) {
            throw new BadRequestException("Password cannot be empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAccountCreated(LocalDateTime.now());
        user.setAccountUpdated(LocalDateTime.now());
        return userRepository.save(user);

    }

    @Override
    public User updateUser(String username, User user) {
        if(user.getUsername() != null) {
            throw new BadRequestException("Username should not provided for self update");
        }
        if(! notNull(user.getPassword())) {
            throw new BadRequestException("Password cannot be empty");
        }
        performFieldChecks(user);
        User oldUser = findByUsername(username);
        oldUser.setFirstName(user.getFirstName());
        oldUser.setLastName(user.getLastName());
        oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
        oldUser.setAccountUpdated(LocalDateTime.now());
        return userRepository.save(oldUser);
    }

    @Override
    public User findByUsername(String email) {
        return userRepository.findByUsername(email);
    }

    public boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public boolean notNull(String value) {
        return value != null && !value.isEmpty();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }

    private void performFieldChecks(User user) {
        if(user.getId() != null) {
            throw new BadRequestException("User id should not be given");
        }
        if(user.getAccountCreated() != null) {
            throw new BadRequestException("Account created should not be given");
        }
        if(user.getAccountUpdated() != null) {
            throw new BadRequestException("Account updated should not be given");
        }
    }
}
