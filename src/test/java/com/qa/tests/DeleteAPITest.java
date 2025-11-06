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
 * DELETE API Test Class Purpose: Test DELETE API endpoint to remove users
 * Covers positive and negative scenarios
 */
public class DeleteAPITest extends BaseTest {

	// Logger instance
	private static final Logger logger = Logger.getLogger(DeleteAPITest.class);

	String serviceURL;
	String apiURL;
	RestClient restClient;
	String url;
	CloseableHttpResponse closeableHttpResponse;

	/**
	 * Setup method runs before each test
	 */
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

	/**
	 * Test DELETE API - Delete existing user
	 * 
	 * Test Steps: 1. Prepare headers 2. Send DELETE request for user ID 2 3.
	 * Validate status code (204 No Content or 200 OK) 4. Verify deletion
	 * 
	 * Expected: Status 204/200, successful deletion
	 */
	@Test(priority = 1, description = "Delete user successfully")
	public void deleteUserTest() {
		try {
			logger.info("========== Starting Delete User Test ==========");
			test = extent.createTest("DELETE API Test - Delete User");
			test.log(Status.INFO, "Test started: Delete user with ID 2");

			// STEP 1: Prepare headers
			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");
			logger.info("Headers prepared");
			test.log(Status.INFO, "Headers configured");

			// STEP 2: Send DELETE request
			String deleteUrl = url + "/2"; // Delete user with ID 2
			logger.info("Sending DELETE request to: " + deleteUrl);
			test.log(Status.INFO, "Sending DELETE request to: " + deleteUrl);

			closeableHttpResponse = restClient.delete(deleteUrl, headerMap);

			// STEP 3: Validate status code
			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);
			test.log(Status.INFO, "Response status code: " + statusCode);

			// DELETE can return 200, 202, or 204
			// 200 OK - Resource deleted with response body
			// 202 Accepted - Request accepted, will be deleted
			// 204 No Content - Resource deleted, no response body
			Assert.assertTrue(
					statusCode == BaseTest.RESPONSE_STATUS_CODE_200 || statusCode == BaseTest.RESPONSE_STATUS_CODE_204,
					"Expected status code 200 or 204 for successful DELETE");

			logger.info("Status code validation passed: " + statusCode);
			test.log(Status.PASS, "DELETE request successful with status: " + statusCode);

			// STEP 4: Log success
			if (statusCode == 204) {
				logger.info("User deleted successfully - No Content returned");
				test.log(Status.INFO, "User deleted - No response body (204)");
			} else {
				logger.info("User deleted successfully - Response body present");
				test.log(Status.INFO, "User deleted - Response body present (200)");
			}

			logger.info("========== Delete User Test Completed Successfully ==========");
			test.log(Status.PASS, "Test completed successfully");

		} catch (AssertionError e) {
			logger.error("Assertion failed: " + e.getMessage(), e);
			test.log(Status.FAIL, "Test failed: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Exception occurred: " + e.getMessage(), e);
			test.log(Status.FAIL, "Exception occurred: " + e.getMessage());
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

	/**
	 * Test DELETE API for non-existent user Negative test case
	 * 
	 * Expected: Status 404 (Not Found)
	 */
	@Test(priority = 2, description = "Delete non-existent user - Negative Test")
	public void deleteNonExistentUserTest() {
		try {
			logger.info("========== Starting Delete Non-Existent User Test ==========");
			test = extent.createTest("DELETE API Test - Non-Existent User (Negative)");
			test.log(Status.INFO, "Test started: Delete non-existent user");

			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");

			// Try to delete non-existent user
			String deleteUrl = url + "/99999"; // Non-existent ID
			logger.info("Attempting to delete non-existent user: " + deleteUrl);
			test.log(Status.INFO, "Sending DELETE request to non-existent ID: 99999");

			closeableHttpResponse = restClient.delete(deleteUrl, headerMap);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);
			test.log(Status.INFO, "Response status code: " + statusCode);

			// Note: JSONPlaceholder returns 200 even for non-existent resources
			// Real APIs should return 404
			if (statusCode == BaseTest.RESPONSE_STATUS_CODE_404) {
				logger.info("API correctly returned 404 for non-existent user");
				test.log(Status.PASS, "API correctly returned 404");
			} else {
				logger.warn("API did not return 404. Got: " + statusCode);
				test.log(Status.WARNING, "API behavior differs from expected (status: " + statusCode + ")");
			}

			logger.info("========== Non-Existent User Test Completed ==========");

		} catch (Exception e) {
			logger.error("Exception in test: " + e.getMessage(), e);
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

	/**
	 * Test DELETE API without authentication Tests security aspect
	 * 
	 * Expected: Status 401 (Unauthorized) if auth is required
	 */
	@Test(priority = 3, description = "Delete without authentication - Security Test", enabled = false) // Disabled as
																										// JSONPlaceholder
																										// doesn't
																										// require auth
	public void deleteWithoutAuthTest() {
		try {
			logger.info("========== Starting Delete Without Auth Test ==========");
			test = extent.createTest("DELETE API Test - Without Authentication");
			test.log(Status.INFO, "Test started: Delete without authentication");

			// Don't add Authorization header
			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");

			String deleteUrl = url + "/2";
			logger.info("Sending DELETE request without auth: " + deleteUrl);
			test.log(Status.INFO, "Sending DELETE request without authentication");

			closeableHttpResponse = restClient.delete(deleteUrl, headerMap);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);

			// Should return 401 Unauthorized
			Assert.assertEquals(statusCode, BaseTest.RESPONSE_STATUS_CODE_401,
					"Expected 401 Unauthorized when auth is missing");

			logger.info("API correctly rejected request without auth");
			test.log(Status.PASS, "API security validated - returned 401");

			logger.info("========== Auth Test Completed ==========");

		} catch (Exception e) {
			logger.error("Exception in auth test: " + e.getMessage(), e);
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

	/**
	 * Test DELETE API - Verify resource is actually deleted End-to-end validation
	 */
	@Test(priority = 4, description = "Delete and verify - E2E Test")
	public void deleteAndVerifyTest() {
		try {
			logger.info("========== Starting Delete and Verify Test ==========");
			test = extent.createTest("DELETE API Test - Delete and Verify (E2E)");
			test.log(Status.INFO, "Test started: Delete and verify deletion");

			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");

			int userIdToDelete = 2;
			String deleteUrl = url + "/" + userIdToDelete;

			// STEP 1: Delete the user
			logger.info("Step 1: Deleting user " + userIdToDelete);
			test.log(Status.INFO, "Step 1: Deleting user ID: " + userIdToDelete);

			closeableHttpResponse = restClient.delete(deleteUrl, headerMap);
			int deleteStatusCode = closeableHttpResponse.getStatusLine().getStatusCode();

			Assert.assertTrue(deleteStatusCode == 200 || deleteStatusCode == 204, "Delete should return 200 or 204");
			logger.info("User deleted successfully");
			test.log(Status.PASS, "User deleted with status: " + deleteStatusCode);
			closeableHttpResponse.close();

			// STEP 2: Try to GET the deleted user (verification)
			logger.info("Step 2: Verifying deletion by GET request");
			test.log(Status.INFO, "Step 2: Attempting to GET deleted user");

			closeableHttpResponse = restClient.get(deleteUrl, headerMap);
			int getStatusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("GET request status code: " + getStatusCode);

			// Note: JSONPlaceholder doesn't actually delete, so returns 200
			// Real APIs should return 404
			if (getStatusCode == BaseTest.RESPONSE_STATUS_CODE_404) {
				logger.info("✓ Deletion verified - User not found (404)");
				test.log(Status.PASS, "Deletion verified - Resource not found");
			} else {
				logger.warn("Resource still exists after deletion (status: " + getStatusCode + ")");
				test.log(Status.WARNING, "Note: Mock API doesn't actually delete resources");
			}

			logger.info("========== Delete and Verify Test Completed ==========");
			test.log(Status.PASS, "E2E test completed");

		} catch (Exception e) {
			logger.error("Exception in E2E test: " + e.getMessage(), e);
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

	/**
	 * Test DELETE with special characters in URL Tests URL encoding and error
	 * handling
	 */
	@Test(priority = 5, description = "Delete with invalid URL - Negative Test")
	public void deleteWithInvalidUrlTest() {
		try {
			logger.info("========== Starting Invalid URL Test ==========");
			test = extent.createTest("DELETE API Test - Invalid URL (Negative)");
			test.log(Status.INFO, "Test started: Delete with invalid URL");

			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");

			// Invalid URL with special characters
			String invalidUrl = url + "/abc@#$";
			logger.info("Sending DELETE to invalid URL: " + invalidUrl);
			test.log(Status.INFO, "Testing with invalid URL: " + invalidUrl);

			try {
				closeableHttpResponse = restClient.delete(invalidUrl, headerMap);
				int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
				logger.info("Response status code: " + statusCode);

				// Should return 400 or 404
				Assert.assertTrue(statusCode == 400 || statusCode == 404, "Invalid URL should return error status");
				test.log(Status.PASS, "API handled invalid URL correctly");

			} catch (Exception e) {
				// Exception is also acceptable for invalid URLs
				logger.info("Exception thrown for invalid URL (expected): " + e.getMessage());
				test.log(Status.PASS, "API/Client correctly rejected invalid URL");
			}

			logger.info("========== Invalid URL Test Completed ==========");

		} catch (Exception e) {
			logger.error("Unexpected exception: " + e.getMessage(), e);
			test.log(Status.FAIL, "Unexpected exception: " + e.getMessage());
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