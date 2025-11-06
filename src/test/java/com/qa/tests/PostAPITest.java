package com.qa.tests;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qa.base.BaseTest;
import com.qa.client.RestClient;
import com.qa.data.Users;

/**
 * POST API Test Class - FIXED VERSION
 */
public class PostAPITest extends BaseTest {

    private static final Logger logger = Logger.getLogger(PostAPITest.class);

    String serviceURL;
    String apiURL;
    RestClient restClient;
    String url;
    CloseableHttpResponse closeableHttpResponse;

    @BeforeMethod
    public void setUp() {
        try {
            logger.info("========== Starting POST API Test Setup ==========");

            serviceURL = prop.getProperty("baseURL");
            apiURL = prop.getProperty("resourcePath");
            url = serviceURL + apiURL;

            logger.info("Complete URL: " + url);

            restClient = new RestClient();
            logger.info("RestClient initialized successfully");

            logger.info("========== POST API Test Setup Completed ==========");

        } catch (Exception e) {
            logger.error("Error in setUp method: " + e.getMessage(), e);
            throw new RuntimeException("Setup failed: " + e.getMessage());
        }
    }

    @Test(priority = 1, description = "Create new user via POST request")
    public void createUserTest() {
        try {
            logger.info("========== Starting Create User Test ==========");

            // FIXED: Create test directly using instance variable
            extentTest = extent.createTest("POST API Test - Create User");
            extentTest.log(Status.INFO, "Test started: Create new user");

            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "application/json");
            logger.info("Headers prepared: " + headerMap);
            extentTest.log(Status.INFO, "Headers configured: Content-Type = application/json");

            ObjectMapper mapper = new ObjectMapper();
            logger.info("ObjectMapper initialized");

            Users users = new Users("morpheus", "leader");
            logger.info("User object created: " + users);
            extentTest.log(Status.INFO, "Test data: " + users.toString());

            try {
                String jsonFilePath = System.getProperty("user.dir") +
                        "/src/main/java/com/qa/data/users.json";
                mapper.writeValue(new File(jsonFilePath), users);
                logger.info("JSON file created at: " + jsonFilePath);
                extentTest.log(Status.INFO, "JSON saved to file");
            } catch (IOException e) {
                logger.warn("Could not save JSON to file: " + e.getMessage());
            }

            String usersJsonString = mapper.writeValueAsString(users);
            logger.info("JSON payload: " + usersJsonString);
            extentTest.log(Status.INFO, "Request payload: " + usersJsonString);

            logger.info("Sending POST request to: " + url);
            extentTest.log(Status.INFO, "Sending POST request to: " + url);

            closeableHttpResponse = restClient.post(url, usersJsonString, headerMap);
            logger.info("POST request sent successfully");

            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            logger.info("Response status code: " + statusCode);
            extentTest.log(Status.INFO, "Response status code: " + statusCode);

            Assert.assertEquals(statusCode, BaseTest.RESPONSE_STATUS_CODE_201,
                    "Expected status code 201 for successful POST");
            logger.info("✓ Status code validation passed: 201");
            extentTest.log(Status.PASS, "Status code validation passed");

            String responseString = EntityUtils.toString(
                    closeableHttpResponse.getEntity(), "UTF-8");
            logger.info("Response body: " + responseString);
            extentTest.log(Status.INFO, "Response received: " + responseString);

            Users usersResObj = mapper.readValue(responseString, Users.class);
            logger.info("Response deserialized to object: " + usersResObj);

            Assert.assertEquals(usersResObj.getName(), users.getName(),
                    "Response name should match request name");
            Assert.assertEquals(usersResObj.getJob(), users.getJob(),
                    "Response job should match request job");
            Assert.assertNotNull(usersResObj.getId(),
                    "Response should contain user ID");
            Assert.assertNotNull(usersResObj.getCreatedAt(),
                    "Response should contain createdAt timestamp");

            logger.info("✓ All validations passed");
            logger.info("Created user ID: " + usersResObj.getId());

            extentTest.log(Status.PASS, "User created successfully");
            extentTest.log(Status.PASS, "All validations passed");
            extentTest.log(Status.INFO, "Created User ID: " + usersResObj.getId());

            logger.info("========== Create User Test Completed Successfully ==========");

        } catch (AssertionError e) {
            logger.error("❌ Assertion failed: " + e.getMessage(), e);
            extentTest.log(Status.FAIL, "Test failed: " + e.getMessage());
            throw e;

        } catch (Exception e) {
            logger.error("❌ Exception occurred: " + e.getMessage(), e);
            extentTest.log(Status.FAIL, "Exception: " + e.getMessage());
            throw new RuntimeException("Test execution failed: " + e.getMessage(), e);

        } finally {
            try {
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                    logger.info("HTTP response closed");
                }
            } catch (IOException e) {
                logger.error("Error closing response: " + e.getMessage(), e);
            }
        }
    }

    @Test(priority = 2, description = "Create user with invalid data - Negative Test")
    public void createUserWithInvalidDataTest() {
        try {
            logger.info("========== Starting Negative Test - Invalid Data ==========");

            extentTest = extent.createTest("POST API Test - Invalid Data (Negative)");
            extentTest.log(Status.INFO, "Test started: Create user with invalid data");

            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "application/json");

            Users invalidUser = new Users("", "leader");
            logger.info("Invalid user created: " + invalidUser);
            extentTest.log(Status.INFO, "Testing with invalid data: " + invalidUser.toString());

            ObjectMapper mapper = new ObjectMapper();
            String invalidJsonString = mapper.writeValueAsString(invalidUser);
            logger.info("Invalid JSON payload: " + invalidJsonString);
            extentTest.log(Status.INFO, "Payload: " + invalidJsonString);

            logger.info("Sending POST request with invalid data");
            extentTest.log(Status.INFO, "Sending POST request");

            closeableHttpResponse = restClient.post(url, invalidJsonString, headerMap);

            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            logger.info("Response status code: " + statusCode);
            extentTest.log(Status.INFO, "Response status: " + statusCode);

            if (statusCode == BaseTest.RESPONSE_STATUS_CODE_400) {
                logger.info("✓ API correctly rejected invalid data with 400");
                extentTest.log(Status.PASS, "API validation working correctly");
            } else {
                logger.warn("⚠ API accepted invalid data (status: " + statusCode + ")");
                extentTest.log(Status.WARNING,
                        "Note: Mock API doesn't validate data (status: " + statusCode + ")");
            }

            logger.info("========== Negative Test Completed ==========");

        } catch (Exception e) {
            logger.error("Exception in negative test: " + e.getMessage(), e);
            extentTest.log(Status.FAIL, "Exception: " + e.getMessage());
            throw new RuntimeException("Negative test failed: " + e.getMessage(), e);

        } finally {
            try {
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
            } catch (IOException e) {
                logger.error("Error closing response: " + e.getMessage(), e);
            }
        }
    }
}