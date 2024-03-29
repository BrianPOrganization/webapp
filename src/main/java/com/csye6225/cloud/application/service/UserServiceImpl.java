package com.csye6225.cloud.application.service;

import com.csye6225.cloud.application.entity.User;
import com.csye6225.cloud.application.exception.BadRequestException;
import com.csye6225.cloud.application.respository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${gcp.email_time_duration}")
    private int tokenExpirationTime;

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
        if(user.getIsVerified() != null && user.getIsVerified()) {
            user.setIsVerified(user.getIsVerified());
        } else {
            user.setIsVerified(false);
        }
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

    @Override
    public Boolean verifyToken(String verificationToken) {
        byte[] decodedBytes = Base64.getDecoder().decode(verificationToken);
        String decodedToken = new String(decodedBytes);
        String token,email = null;
        LocalDateTime createdTime = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> tokenData = mapper.readValue(decodedToken, Map.class);
            token = tokenData.get("token");
            email = tokenData.get("email");
            String dateTime = tokenData.get("createdTime").replace(" ", "T");
            createdTime = LocalDateTime.parse(dateTime);
        } catch (Exception e) {
            logger.error("Error decoding token: (Invalid format) " + e.getMessage());
            throw new BadRequestException("Invalid token format");
        }

        User user = userRepository.findByUsername(email);
        if (user == null) {
            return false;
        }
        user.setEmailVerifiedAt(LocalDateTime.now(ZoneOffset.UTC));
        if (user.getToken().equals(token)) {
            if(Duration.between(createdTime, LocalDateTime.now(ZoneOffset.UTC)).toMinutes() <= tokenExpirationTime) {
                user.setIsVerified(true);
            } else {
                logger.error("Token expired");
                return false;
            }
        } else {
            logger.error("Invalid token");
            return false;
        }
        userRepository.save(user);
        return user.getIsVerified();
    }

    @Override
    public boolean isUserVerified(String username) {
        User user = findByUsername(username);
        return user.getIsVerified();
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
