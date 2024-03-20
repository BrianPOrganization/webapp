package com.csye6225.cloud.application.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionResponse {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionResponse.class);

    private String message;

    public ExceptionResponse(String message) {
        this.message = message;
        logger.error("ExceptionResponse created with message: {}", message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
