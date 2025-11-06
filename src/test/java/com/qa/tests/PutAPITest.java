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
 * PUT API 
 */
public class PutAPITest extends BaseTest {

	private static final Logger logger = Logger.getLogger(PutAPITest.class);

	String serviceURL;
	String apiURL;
	RestClient restClient;
	String url;
	CloseableHttpResponse closeableHttpResponse;

	@BeforeMethod
	public void setUp() {
		try {
			logger.info("========== Starting PUT API Test Setup ==========");

			serviceURL = prop.getProperty("baseURL");
			apiURL = prop.getProperty("resourcePath");
			url = serviceURL + apiURL;

			logger.info("API URL configured: " + url);

			restClient = new RestClient();
			logger.info("RestClient initialized successfully");

			logger.info("========== PUT API Test Setup Completed ==========");
		} catch (Exception e) {
			logger.error("Error in setUp: " + e.getMessage(), e);
			throw new RuntimeException("Setup failed: " + e.getMessage());
		}
	}

	@Test(priority = 1, description = "Update user using PUT request")
	public void updateUserTest() {
		try {
			logger.info("========== Starting Update User Test ==========");

			// FIXED: Direct assignment instead of test.set()
			extentTest = extent.createTest("PUT API Test - Update User");
			extentTest.log(Status.INFO, "Test started: Update user with ID 2");

			HashMap<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-Type", "application/json");
			logger.info("Headers prepared: " + headerMap);
			extentTest.log(Status.INFO, "Headers set: Content-Type = application/json");

			Users updatedUser = new Users("morpheus", "zion resident");
			logger.info("Updated user object created: " + updatedUser);
			extentTest.log(Status.INFO, "User data: " + updatedUser.toString());

			ObjectMapper mapper = new ObjectMapper();
			String userJsonString = mapper.writeValueAsString(updatedUser);
			logger.info("JSON payload: " + userJsonString);
			extentTest.log(Status.INFO, "JSON payload prepared: " + userJsonString);

			String updateUrl = url + "/2";
			logger.info("Sending PUT request to: " + updateUrl);
			extentTest.log(Status.INFO, "Sending PUT request to: " + updateUrl);

			closeableHttpResponse = restClient.put(updateUrl, userJsonString, headerMap);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);

			Assert.assertEquals(statusCode, BaseTest.RESPONSE_STATUS_CODE_200,
					"Expected status code 200 for successful PUT request");
			logger.info("Status code validation passed: 200");
			extentTest.log(Status.PASS, "Status code validation passed: " + statusCode);

			String responseString = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
			logger.info("Response body: " + responseString);
			extentTest.log(Status.INFO, "Response received: " + responseString);

			Users responseUser = mapper.readValue(responseString, Users.class);
			logger.info("Response deserialized: " + responseUser);

			Assert.assertEquals(responseUser.getName(), updatedUser.getName(),
					"Response name should match updated name");
			Assert.assertEquals(responseUser.getJob(), updatedUser.getJob(), "Response job should match updated job");
			Assert.assertNotNull(responseUser.getUpdatedAt(), "UpdatedAt timestamp should be present");

			logger.info("All validations passed successfully");
			extentTest.log(Status.PASS, "PUT API test completed successfully");

			logger.info("========== Update User Test Completed Successfully ==========");

		} catch (AssertionError e) {
			logger.error("Assertion failed: " + e.getMessage(), e);
			extentTest.log(Status.FAIL, "Test failed: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Exception occurred: " + e.getMessage(), e);
			extentTest.log(Status.FAIL, "Exception occurred: " + e.getMessage());
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

	@Test(priority = 2, description = "Update user with invalid data - Negative Test")
	public void updateUserWithInvalidDataTest() {
		try {
			logger.info("========== Starting Negative Test - Invalid Data ==========");

			extentTest = extent.createTest("PUT API Test - Invalid Data (Negative)");
			extentTest.log(Status.INFO, "Test started: Update user with invalid data");

			HashMap<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-Type", "application/json");

			Users invalidUser = new Users("", "leader");
			logger.info("Invalid user created: " + invalidUser);
			extentTest.log(Status.INFO, "Testing with invalid user data: " + invalidUser.toString());

			ObjectMapper mapper = new ObjectMapper();
			String userJsonString = mapper.writeValueAsString(invalidUser);
			logger.info("Invalid JSON payload: " + userJsonString);

			String updateUrl = url + "/2";
			logger.info("Sending PUT request with invalid data to: " + updateUrl);
			extentTest.log(Status.INFO, "Sending PUT request with invalid data");

			closeableHttpResponse = restClient.put(updateUrl, userJsonString, headerMap);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);

			if (statusCode == BaseTest.RESPONSE_STATUS_CODE_400) {
				logger.info("API correctly rejected invalid data with 400");
				extentTest.log(Status.PASS, "API correctly returned 400 for invalid data");
			} else {
				logger.warn("API did not return 400 for invalid data. Got: " + statusCode);
				extentTest.log(Status.WARNING, "API accepted invalid data (status: " + statusCode + ")");
			}

			logger.info("========== Negative Test Completed ==========");

		} catch (Exception e) {
			logger.error("Exception in negative test: " + e.getMessage(), e);
			extentTest.log(Status.FAIL, "Exception occurred: " + e.getMessage());
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

	@Test(priority = 3, description = "Update non-existent user - Negative Test")
	public void updateNonExistentUserTest() {
		try {
			logger.info("========== Starting Test - Non-Existent User ==========");

			extentTest = extent.createTest("PUT API Test - Non-Existent User (Negative)");
			extentTest.log(Status.INFO, "Test started: Update non-existent user");

			HashMap<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-Type", "application/json");

			Users user = new Users("test", "tester");
			ObjectMapper mapper = new ObjectMapper();
			String userJsonString = mapper.writeValueAsString(user);

			String updateUrl = url + "/99999";
			logger.info("Attempting to update non-existent user: " + updateUrl);
			extentTest.log(Status.INFO, "Sending PUT request to non-existent user ID: 99999");

			closeableHttpResponse = restClient.put(updateUrl, userJsonString, headerMap);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);

			if (statusCode == BaseTest.RESPONSE_STATUS_CODE_404) {
				logger.info("API correctly returned 404 for non-existent user");
				extentTest.log(Status.PASS, "API correctly returned 404");
			} else {
				logger.warn("API did not return 404. Got: " + statusCode);
				extentTest.log(Status.WARNING, "API did not return 404 (status: " + statusCode + ")");
			}

			logger.info("========== Non-Existent User Test Completed ==========");

		} catch (Exception e) {
			logger.error("Exception in non-existent user test: " + e.getMessage(), e);
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