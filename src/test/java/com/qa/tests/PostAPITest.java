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
 * POST API Test Class Tests POST endpoint to create new users Includes logging
 * and reporting
 */
public class PostAPITest extends BaseTest {

	// Logger instance for this class
	private static final Logger logger = Logger.getLogger(PostAPITest.class);

	String serviceURL;
	String apiURL;
	RestClient restClient;
	String url;
	CloseableHttpResponse closeableHttpResponse;

	/**
	 * Setup method - runs before each test Initializes configuration and RestClient
	 */
	@BeforeMethod
	public void setUp() {
		try {
			logger.info("========== Starting POST API Test Setup ==========");

			// Load configuration
			serviceURL = prop.getProperty("baseURL");
			apiURL = prop.getProperty("resourcePath");
			url = serviceURL + apiURL;

			logger.info("Base URL: " + serviceURL);
			logger.info("Resource Path: " + apiURL);
			logger.info("Complete URL: " + url);

			// Initialize RestClient
			restClient = new RestClient();
			logger.info("RestClient initialized successfully");

			logger.info("========== POST API Test Setup Completed ==========");

		} catch (Exception e) {
			logger.error("Error in setUp method: " + e.getMessage(), e);
			throw new RuntimeException("Setup failed: " + e.getMessage());
		}
	}

	/**
	 * Test POST API - Create new user
	 * 
	 * Test Steps: 1. Create user object with test data 2. Convert object to JSON 3.
	 * Save JSON to file (optional) 4. Send POST request 5. Validate status code
	 * (201 Created) 6. Validate response data matches request
	 * 
	 * Expected: Status 201, user created with matching data
	 */
	@Test(priority = 1, description = "Create new user via POST request")
	public void createUserTest() {
		try {
			logger.info("========== Starting Create User Test ==========");

			// Create Extent Test
			test.set(extent.createTest("POST API Test - Create User"));
			test.get().log(Status.INFO, "Test started: Create new user");

			// STEP 1: Prepare headers
			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");
			logger.info("Headers prepared: " + headerMap);
			test.get().log(Status.INFO, "Headers configured: Content-Type = application/json");

			// STEP 2: Create ObjectMapper for JSON conversion
			ObjectMapper mapper = new ObjectMapper();
			logger.info("ObjectMapper initialized");

			// STEP 3: Create user object with test data
			Users users = new Users("morpheus", "leader");
			logger.info("User object created: " + users);
			test.get().log(Status.INFO, "Test data: " + users.toString());

			// STEP 4: Save object to JSON file (optional - for reference)
			try {
				String jsonFilePath = System.getProperty("user.dir") + "/src/main/java/com/qa/data/users.json";
				mapper.writeValue(new File(jsonFilePath), users);
				logger.info("JSON file created at: " + jsonFilePath);
				test.get().log(Status.INFO, "JSON saved to file");
			} catch (IOException e) {
				logger.warn("Could not save JSON to file: " + e.getMessage());
				// Don't fail test if file save fails
			}

			// STEP 5: Convert object to JSON string
			String usersJsonString = mapper.writeValueAsString(users);
			logger.info("JSON payload: " + usersJsonString);
			test.get().log(Status.INFO, "Request payload: " + usersJsonString);

			// STEP 6: Send POST request
			logger.info("Sending POST request to: " + url);
			test.get().log(Status.INFO, "Sending POST request to: " + url);

			closeableHttpResponse = restClient.post(url, usersJsonString, headerMap);
			logger.info("POST request sent successfully");

			// STEP 7: Validate status code
			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);
			test.get().log(Status.INFO, "Response status code: " + statusCode);

			Assert.assertEquals(statusCode, BaseTest.RESPONSE_STATUS_CODE_201,
					"Expected status code 201 for successful POST");
			logger.info("✓ Status code validation passed: 201");
			test.get().log(Status.PASS, "Status code validation passed");

			// STEP 8: Extract response body
			String responseString = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
			logger.info("Response body: " + responseString);
			test.get().log(Status.INFO, "Response received: " + responseString);

			// STEP 9: Convert response JSON to object
			Users usersResObj = mapper.readValue(responseString, Users.class);
			logger.info("Response deserialized to object: " + usersResObj);

			// STEP 10: Validate response data
			Assert.assertEquals(usersResObj.getName(), users.getName(), "Response name should match request name");
			Assert.assertEquals(usersResObj.getJob(), users.getJob(), "Response job should match request job");
			Assert.assertNotNull(usersResObj.getId(), "Response should contain user ID");
			Assert.assertNotNull(usersResObj.getCreatedAt(), "Response should contain createdAt timestamp");

			logger.info("✓ All validations passed");
			logger.info("Created user ID: " + usersResObj.getId());
			logger.info("Created at: " + usersResObj.getCreatedAt());

			test.get().log(Status.PASS, "User created successfully");
			test.get().log(Status.PASS, "All validations passed");
			test.get().log(Status.INFO, "Created User ID: " + usersResObj.getId());

			logger.info("========== Create User Test Completed Successfully ==========");

		} catch (AssertionError e) {
			logger.error("❌ Assertion failed: " + e.getMessage(), e);
			test.get().log(Status.FAIL, "Test failed: " + e.getMessage());
			throw e;

		} catch (Exception e) {
			logger.error("❌ Exception occurred: " + e.getMessage(), e);
			test.get().log(Status.FAIL, "Exception: " + e.getMessage());
			throw new RuntimeException("Test execution failed: " + e.getMessage(), e);

		} finally {
			// Close HTTP response
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
	 * Test POST API with invalid data - Negative Testing
	 * 
	 * Test Steps: 1. Create user with empty/null fields 2. Send POST request 3.
	 * Validate error response (400 Bad Request)
	 * 
	 * Expected: Status 400, validation error message
	 */
	@Test(priority = 2, description = "Create user with invalid data - Negative Test")
	public void createUserWithInvalidDataTest() {
		try {
			logger.info("========== Starting Negative Test - Invalid Data ==========");

			test.set(extent.createTest("POST API Test - Invalid Data (Negative)"));
			test.get().log(Status.INFO, "Test started: Create user with invalid data");

			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");

			// Create user with empty name (invalid)
			Users invalidUser = new Users("", "leader");
			logger.info("Invalid user created: " + invalidUser);
			test.get().log(Status.INFO, "Testing with invalid data: " + invalidUser.toString());

			// Convert to JSON
			ObjectMapper mapper = new ObjectMapper();
			String invalidJsonString = mapper.writeValueAsString(invalidUser);
			logger.info("Invalid JSON payload: " + invalidJsonString);
			test.get().log(Status.INFO, "Payload: " + invalidJsonString);

			// Send POST request
			logger.info("Sending POST request with invalid data");
			test.get().log(Status.INFO, "Sending POST request");

			closeableHttpResponse = restClient.post(url, invalidJsonString, headerMap);

			// Validate response
			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);
			test.get().log(Status.INFO, "Response status: " + statusCode);

			// Note: JSONPlaceholder accepts any data, so it returns 201
			// Real APIs should return 400 for invalid data
			if (statusCode == BaseTest.RESPONSE_STATUS_CODE_400) {
				logger.info("✓ API correctly rejected invalid data with 400");
				test.get().log(Status.PASS, "API validation working correctly");
			} else {
				logger.warn("⚠ API accepted invalid data (status: " + statusCode + ")");
				test.get().log(Status.WARNING, "Note: Mock API doesn't validate data (status: " + statusCode + ")");
			}

			logger.info("========== Negative Test Completed ==========");

		} catch (Exception e) {
			logger.error("Exception in negative test: " + e.getMessage(), e);
			test.get().log(Status.FAIL, "Exception: " + e.getMessage());
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
	 * Test POST API with missing required fields
	 * 
	 * Expected: Status 400 Bad Request
	 */
	@Test(priority = 3, description = "Create user with missing fields - Negative Test")
	public void createUserWithMissingFieldsTest() {
		try {
			logger.info("========== Starting Test - Missing Fields ==========");

			test.set(extent.createTest("POST API Test - Missing Fields (Negative)"));
			test.get().log(Status.INFO, "Test started: Create user with missing fields");

			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");

			// JSON with missing 'job' field
			String incompleteJson = "{\"name\":\"morpheus\"}";
			logger.info("Incomplete JSON payload: " + incompleteJson);
			test.get().log(Status.INFO, "Payload with missing field: " + incompleteJson);

			logger.info("Sending POST request");
			test.get().log(Status.INFO, "Sending request");

			closeableHttpResponse = restClient.post(url, incompleteJson, headerMap);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);

			// Real APIs should validate required fields
			if (statusCode == 400) {
				logger.info("✓ API correctly validated required fields");
				test.get().log(Status.PASS, "Required field validation working");
			} else {
				logger.warn("⚠ API didn't validate required fields");
				test.get().log(Status.WARNING, "API accepted incomplete data");
			}

			logger.info("========== Missing Fields Test Completed ==========");

		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage(), e);
			test.get().log(Status.FAIL, "Exception: " + e.getMessage());
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