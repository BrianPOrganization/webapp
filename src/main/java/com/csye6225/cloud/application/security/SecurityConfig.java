package com.csye6225.cloud.application.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        security.
                authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers("/healthz").permitAll()
                        .requestMatchers("/v5/user").permitAll()
                        .requestMatchers("verify").permitAll()
                        .anyRequest().authenticated()
        )
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(withDefaults());
        return security.build();
    }
}
