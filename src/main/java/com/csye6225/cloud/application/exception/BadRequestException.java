package com.csye6225.cloud.application.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BadRequestException extends RuntimeException{

    private static final Logger logger = LoggerFactory.getLogger(BadRequestException.class);
    public BadRequestException(String message) {
        super(message);
        logger.error("BadRequestException occurred with message: {}", message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        logger.error("BadRequestException occurred with message: {}", message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
        logger.error("BadRequestException occurred", cause);
    }
}
