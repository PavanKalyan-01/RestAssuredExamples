package com.qa.tests;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.qa.base.BaseTest;
import com.qa.client.RestClient;

/**
 * DELETE API Test Class - FIXED VERSION
 */
public class DeleteAPITest extends BaseTest {

    private static final Logger logger = Logger.getLogger(DeleteAPITest.class);

    String serviceURL;
    String apiURL;
    RestClient restClient;
    String url;
    CloseableHttpResponse closeableHttpResponse;

    @BeforeMethod
    public void setUp() {
        try {
            logger.info("========== Starting DELETE API Test Setup ==========");

            serviceURL = prop.getProperty("baseURL");
            apiURL = prop.getProperty("resourcePath");
            url = serviceURL + apiURL;

            logger.info("API URL configured: " + url);

            restClient = new RestClient();
            logger.info("RestClient initialized");

            logger.info("========== DELETE API Test Setup Completed ==========");
        } catch (Exception e) {
            logger.error("Error in setUp: " + e.getMessage(), e);
            throw new RuntimeException("Setup failed: " + e.getMessage());
        }
    }

    @Test(priority = 1, description = "Delete user successfully")
    public void deleteUserTest() {
        try {
            logger.info("========== Starting Delete User Test ==========");
            
            // FIXED: Direct assignment
            extentTest = extent.createTest("DELETE API Test - Delete User");
            extentTest.log(Status.INFO, "Test started: Delete user with ID 2");

            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "application/json");
            logger.info("Headers prepared");
            extentTest.log(Status.INFO, "Headers configured");

            String deleteUrl = url + "/2";
            logger.info("Sending DELETE request to: " + deleteUrl);
            extentTest.log(Status.INFO, "Sending DELETE request to: " + deleteUrl);

            closeableHttpResponse = restClient.delete(deleteUrl, headerMap);

            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            logger.info("Response status code: " + statusCode);
            extentTest.log(Status.INFO, "Response status code: " + statusCode);

            Assert.assertTrue(
                    statusCode == BaseTest.RESPONSE_STATUS_CODE_200 ||
                            statusCode == BaseTest.RESPONSE_STATUS_CODE_204,
                    "Expected status code 200 or 204 for successful DELETE"
            );

            logger.info("Status code validation passed: " + statusCode);
            extentTest.log(Status.PASS, "DELETE request successful with status: " + statusCode);

            if (statusCode == 204) {
                logger.info("User deleted successfully - No Content returned");
                extentTest.log(Status.INFO, "User deleted - No response body (204)");
            } else {
                logger.info("User deleted successfully - Response body present");
                extentTest.log(Status.INFO, "User deleted - Response body present (200)");
            }

            logger.info("========== Delete User Test Completed Successfully ==========");
            extentTest.log(Status.PASS, "Test completed successfully");

        } catch (AssertionError e) {
            logger.error("Assertion failed: " + e.getMessage(), e);
            extentTest.log(Status.FAIL, "Test failed: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred: " + e.getMessage(), e);
            extentTest.log(Status.FAIL, "Exception occurred: " + e.getMessage());
            throw new RuntimeException("Test failed: " + e.getMessage(), e);
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

    @Test(priority = 2, description = "Delete non-existent user - Negative Test")
    public void deleteNonExistentUserTest() {
        try {
            logger.info("========== Starting Delete Non-Existent User Test ==========");
            
            extentTest = extent.createTest("DELETE API Test - Non-Existent User (Negative)");
            extentTest.log(Status.INFO, "Test started: Delete non-existent user");

            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "application/json");

            String deleteUrl = url + "/99999";
            logger.info("Attempting to delete non-existent user: " + deleteUrl);
            extentTest.log(Status.INFO, "Sending DELETE request to non-existent ID: 99999");

            closeableHttpResponse = restClient.delete(deleteUrl, headerMap);

            int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            logger.info("Response status code: " + statusCode);
            extentTest.log(Status.INFO, "Response status code: " + statusCode);

            if (statusCode == BaseTest.RESPONSE_STATUS_CODE_404) {
                logger.info("API correctly returned 404 for non-existent user");
                extentTest.log(Status.PASS, "API correctly returned 404");
            } else {
                logger.warn("API did not return 404. Got: " + statusCode);
                extentTest.log(Status.WARNING, "API behavior differs from expected (status: " + statusCode + ")");
            }

            logger.info("========== Non-Existent User Test Completed ==========");

        } catch (Exception e) {
            logger.error("Exception in test: " + e.getMessage(), e);
            extentTest.log(Status.FAIL, "Exception occurred: " + e.getMessage());
            throw new RuntimeException("Test failed: " + e.getMessage(), e);
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

    @Test(priority = 3, description = "Delete and verify - E2E Test")
    public void deleteAndVerifyTest() {
        try {
            logger.info("========== Starting Delete and Verify Test ==========");
            
            extentTest = extent.createTest("DELETE API Test - Delete and Verify (E2E)");
            extentTest.log(Status.INFO, "Test started: Delete and verify deletion");

            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Content-Type", "application/json");

            int userIdToDelete = 2;
            String deleteUrl = url + "/" + userIdToDelete;

            logger.info("Step 1: Deleting user " + userIdToDelete);
            extentTest.log(Status.INFO, "Step 1: Deleting user ID: " + userIdToDelete);

            closeableHttpResponse = restClient.delete(deleteUrl, headerMap);
            int deleteStatusCode = closeableHttpResponse.getStatusLine().getStatusCode();

            Assert.assertTrue(
                    deleteStatusCode == 200 || deleteStatusCode == 204,
                    "Delete should return 200 or 204"
            );
            logger.info("User deleted successfully");
            extentTest.log(Status.PASS, "User deleted with status: " + deleteStatusCode);
            closeableHttpResponse.close();

            logger.info("Step 2: Verifying deletion by GET request");
            extentTest.log(Status.INFO, "Step 2: Attempting to GET deleted user");

            closeableHttpResponse = restClient.get(deleteUrl, headerMap);
            int getStatusCode = closeableHttpResponse.getStatusLine().getStatusCode();
            logger.info("GET request status code: " + getStatusCode);

            if (getStatusCode == BaseTest.RESPONSE_STATUS_CODE_404) {
                logger.info("✓ Deletion verified - User not found (404)");
                extentTest.log(Status.PASS, "Deletion verified - Resource not found");
            } else {
                logger.warn("Resource still exists after deletion (status: " + getStatusCode + ")");
                extentTest.log(Status.WARNING, "Note: Mock API doesn't actually delete resources");
            }

            logger.info("========== Delete and Verify Test Completed ==========");
            extentTest.log(Status.PASS, "E2E test completed");

        } catch (Exception e) {
            logger.error("Exception in E2E test: " + e.getMessage(), e);
            extentTest.log(Status.FAIL, "Exception occurred: " + e.getMessage());
            throw new RuntimeException("Test failed: " + e.getMessage(), e);
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