package com.csye6225.cloud.application.service;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class HealthCheckService {

    private final DataSource dataSource;

    public HealthCheckService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean getDBHealth() {
        try {
            Connection connection = dataSource.getConnection();
            connection.createStatement().execute("Select 1");
            return true;
        } catch(SQLException e) {
            return false;
        }
    }
}
