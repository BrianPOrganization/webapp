package com.csye6225.cloud.application;

import com.csye6225.cloud.application.service.UserService;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class IntegrationTests {

    private static Logger logger = LoggerFactory.getLogger(IntegrationTests.class);

    @Autowired
    UserService userService;

    @Autowired
    DataSource dataSource;

    private String id;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void testCreateAndAccountExists() {
        System.out.println(baseUrl);
        logger.info("Base URL is " + baseUrl);
        RestAssured
                .given()
                .contentType("application/json")
                .body("{\"username\":\"user1@test.com\"," +
                        "\"password\":\"password\"," +
                        "\"firstName\":\"user\"," +
                        "\"lastName\":\"1\"," +
                        "\"isVerified\":true}")
                .when()
                .post(baseUrl + "/v5/user")
                .then()
                .body("userName", equalTo("user1@test.com"))
                .body("firstName", equalTo("user"))
                .body("lastName", equalTo("1"));


        RestAssured
                .given()
                .contentType("application/json")
                .auth().basic("user1@test.com", "password")
                .when()
                .get(baseUrl + "/v5/user/self").then().
                statusCode(200)
                .body("userName", equalTo("user1@test.com"))
                .body("firstName", equalTo("user"))
                .body("lastName", equalTo("1"));
    }

    @Test
    void testUpdateAndAccountExists() {
        RestAssured
                .given()
                .contentType("application/json")
                .auth().basic("user1@test.com", "password")
                .body("{\"password\":\"password1\"," +
                        "\"firstName\":\"userupdated\"," +
                        "\"lastName\":\"2\"," +
                        "\"isVerified\":true}")
                .when()
                .put(baseUrl + "/v5/user/self")
                .then().
                statusCode(204);

        RestAssured
                .given()
                .contentType("application/json")
                .auth().basic("user1@test.com", "password1")
                .when()
                .get(baseUrl + "/v5/user/self").then().
                statusCode(200)
                .body("userName", equalTo("user1@test.com"))
                .body("firstName", equalTo("userupdated"))
                .body("lastName", equalTo("2"));
    }

}
