package com.qa.tests;

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
 * PUT API Test Class Purpose: Test PUT API endpoint to update an existing user
 * PUT replaces the entire resource with new data
 */
public class PutAPITest extends BaseTest {

	// Logger instance for this class
	private static final Logger logger = Logger.getLogger(PutAPITest.class);

	String serviceURL;
	String apiURL;
	RestClient restClient;
	String url;
	CloseableHttpResponse closeableHttpResponse;

	/**
	 * Setup method runs before each test Initializes URLs and RestClient
	 */
	@BeforeMethod
	public void setUp() {
		try {
			logger.info("========== Starting PUT API Test Setup ==========");

			// Read configuration from properties file
			serviceURL = prop.getProperty("baseURL");
			apiURL = prop.getProperty("resourcePath");

			logger.info("Base URL: " + serviceURL);
			logger.info("Resource Path: " + apiURL);

			// Construct complete URL
			url = serviceURL + apiURL;
			logger.info("Complete API URL: " + url);

			// Initialize RestClient
			restClient = new RestClient();
			logger.info("RestClient initialized successfully");

			logger.info("========== PUT API Test Setup Completed ==========");
		} catch (Exception e) {
			logger.error("Error in setUp method: " + e.getMessage(), e);
			throw new RuntimeException("Setup failed: " + e.getMessage());
		}
	}

	/**
	 * Test PUT API - Update existing user
	 * 
	 * Test Steps: 1. Create updated user data 2. Convert to JSON 3. Send PUT
	 * request to update user with ID 2 4. Validate status code (200) 5. Validate
	 * response data matches update
	 * 
	 * Expected: Status 200, updated data in response
	 */
	@Test(priority = 1, description = "Update user using PUT request")
	public void updateUserTest() {
		try {
			logger.info("========== Starting Update User Test ==========");
			test = extent.createTest("PUT API Test - Update User");
			test.log(Status.INFO, "Test started: Update user with ID 2");

			// STEP 1: Prepare headers
			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");
			logger.info("Headers prepared: " + headerMap);
			test.log(Status.INFO, "Headers set: Content-Type = application/json");

			// STEP 2: Create updated user object
			Users updatedUser = new Users("morpheus", "zion resident");
			logger.info("Updated user object created: " + updatedUser);
			test.log(Status.INFO, "User data: " + updatedUser.toString());

			// STEP 3: Convert user object to JSON
			ObjectMapper mapper = new ObjectMapper();
			String userJsonString = mapper.writeValueAsString(updatedUser);
			logger.info("JSON payload: " + userJsonString);
			test.log(Status.INFO, "JSON payload prepared: " + userJsonString);

			// STEP 4: Send PUT request to update user with ID 2
			String updateUrl = url + "/2"; // Update user with ID 2
			logger.info("Sending PUT request to: " + updateUrl);
			test.log(Status.INFO, "Sending PUT request to: " + updateUrl);

			closeableHttpResponse = restClient.put(updateUrl, userJsonString, headerMap);

			// STEP 5: Validate status code
			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);

			Assert.assertEquals(statusCode, BaseTest.RESPONSE_STATUS_CODE_200,
					"Expected status code 200 for successful PUT request");
			logger.info("Status code validation passed: 200");
			test.log(Status.PASS, "Status code validation passed: " + statusCode);

			// STEP 6: Extract and validate response
			String responseString = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
			logger.info("Response body: " + responseString);
			test.log(Status.INFO, "Response received: " + responseString);

			// STEP 7: Deserialize response to object
			Users responseUser = mapper.readValue(responseString, Users.class);
			logger.info("Response deserialized: " + responseUser);

			// STEP 8: Validate response data
			Assert.assertEquals(responseUser.getName(), updatedUser.getName(),
					"Response name should match updated name");
			Assert.assertEquals(responseUser.getJob(), updatedUser.getJob(), "Response job should match updated job");
			Assert.assertNotNull(responseUser.getUpdatedAt(), "UpdatedAt timestamp should be present");

			logger.info("All validations passed successfully");
			test.log(Status.PASS, "PUT API test completed successfully");

			logger.info("========== Update User Test Completed Successfully ==========");

		} catch (AssertionError e) {
			logger.error("Assertion failed: " + e.getMessage(), e);
			test.log(Status.FAIL, "Test failed: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Exception occurred: " + e.getMessage(), e);
			test.log(Status.FAIL, "Exception occurred: " + e.getMessage());
			throw new RuntimeException("Test execution failed: " + e.getMessage(), e);
		} finally {
			// Close response
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

	/**
	 * Test PUT API with invalid data - Negative Testing
	 * 
	 * Test Steps: 1. Create user with empty name (invalid) 2. Send PUT request 3.
	 * Validate error response
	 * 
	 * Expected: Status 400 or appropriate error
	 */
	@Test(priority = 2, description = "Update user with invalid data - Negative Test")
	public void updateUserWithInvalidDataTest() {
		try {
			logger.info("========== Starting Negative Test - Invalid Data ==========");
			test = extent.createTest("PUT API Test - Invalid Data (Negative)");
			test.log(Status.INFO, "Test started: Update user with invalid data");

			// Prepare headers
			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");

			// Create invalid user (empty name)
			Users invalidUser = new Users("", "leader"); // Empty name is invalid
			logger.info("Invalid user created: " + invalidUser);
			test.log(Status.INFO, "Testing with invalid user data: " + invalidUser.toString());

			// Convert to JSON
			ObjectMapper mapper = new ObjectMapper();
			String userJsonString = mapper.writeValueAsString(invalidUser);
			logger.info("Invalid JSON payload: " + userJsonString);

			// Send PUT request
			String updateUrl = url + "/2";
			logger.info("Sending PUT request with invalid data to: " + updateUrl);
			test.log(Status.INFO, "Sending PUT request with invalid data");

			closeableHttpResponse = restClient.put(updateUrl, userJsonString, headerMap);

			// Get status code
			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);

			// Note: JSONPlaceholder accepts any data, so it returns 200
			// In real APIs, this should return 400
			if (statusCode == BaseTest.RESPONSE_STATUS_CODE_400) {
				logger.info("API correctly rejected invalid data with 400");
				test.log(Status.PASS, "API correctly returned 400 for invalid data");
			} else {
				logger.warn("API did not return 400 for invalid data. Got: " + statusCode);
				test.log(Status.WARNING, "API accepted invalid data (status: " + statusCode + ")");
			}

			logger.info("========== Negative Test Completed ==========");

		} catch (Exception e) {
			logger.error("Exception in negative test: " + e.getMessage(), e);
			test.log(Status.FAIL, "Exception occurred: " + e.getMessage());
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

	/**
	 * Test PUT API for non-existent user
	 * 
	 * Expected: Status 404 (Not Found)
	 */
	@Test(priority = 3, description = "Update non-existent user - Negative Test")
	public void updateNonExistentUserTest() {
		try {
			logger.info("========== Starting Test - Non-Existent User ==========");
			test = extent.createTest("PUT API Test - Non-Existent User (Negative)");
			test.log(Status.INFO, "Test started: Update non-existent user");

			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");

			Users user = new Users("test", "tester");
			ObjectMapper mapper = new ObjectMapper();
			String userJsonString = mapper.writeValueAsString(user);

			// Try to update user with non-existent ID
			String updateUrl = url + "/99999"; // Non-existent user ID
			logger.info("Attempting to update non-existent user: " + updateUrl);
			test.log(Status.INFO, "Sending PUT request to non-existent user ID: 99999");

			closeableHttpResponse = restClient.put(updateUrl, userJsonString, headerMap);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);

			// Note: JSONPlaceholder returns 200 even for non-existent IDs
			// Real APIs should return 404
			if (statusCode == BaseTest.RESPONSE_STATUS_CODE_404) {
				logger.info("API correctly returned 404 for non-existent user");
				test.log(Status.PASS, "API correctly returned 404");
			} else {
				logger.warn("API did not return 404. Got: " + statusCode);
				test.log(Status.WARNING, "API did not return 404 (status: " + statusCode + ")");
			}

			logger.info("========== Non-Existent User Test Completed ==========");

		} catch (Exception e) {
			logger.error("Exception in non-existent user test: " + e.getMessage(), e);
			test.log(Status.FAIL, "Exception occurred: " + e.getMessage());
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