package com.csye6225.cloud.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class HealthCheckService {

    private final Logger logger = LoggerFactory.getLogger(HealthCheckService.class);

    private final DataSource dataSource;

    public HealthCheckService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean getDBHealth() {
        try (Connection connection = dataSource.getConnection()){
            connection.createStatement().execute("Select 1");
            dataSource.getConnection().close();
            return true;
        } catch(SQLException e) {
            logger.error("Error connecting to database: " + e.getMessage());
            return false;
        }
    }
}
