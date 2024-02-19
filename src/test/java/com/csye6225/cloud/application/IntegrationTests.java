package com.csye6225.cloud.application;

import com.csye6225.cloud.application.service.UserService;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        RestAssured
                .given()
                .contentType("application/json")
                .body("{\"username\":\"user1@test.com\"," +
                        "\"password\":\"password\"," +
                        "\"firstName\":\"user\"," +
                        "\"lastName\":\"1\"}")
                .when()
                .post(baseUrl + "/v1/user")
                .then()
                .body("userName", equalTo("user1@test.com"))
                .body("firstName", equalTo("user"))
                .body("lastName", equalTo("1"));


        RestAssured
                .given()
                .contentType("application/json")
                .auth().basic("user1@test.com", "password")
                .when()
                .get(baseUrl + "/v1/user/self").then().
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
                        "\"lastName\":\"2\"}")
                .when()
                .put(baseUrl + "/v1/user/self")
                .then().
                statusCode(200)
                .body("userName", equalTo("user1@test.com"))
                .body("firstName", equalTo("userupdated"))
                .body("lastName", equalTo("2"));

        RestAssured
                .given()
                .contentType("application/json")
                .auth().basic("user1@test.com", "password1")
                .when()
                .get(baseUrl + "/v1/user/self").then().
                statusCode(200)
                .body("userName", equalTo("user1@test.com"))
                .body("firstName", equalTo("userupdatedzzz"))
                .body("lastName", equalTo("2"));
    }

}
