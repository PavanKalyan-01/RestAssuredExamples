package com.qa.tests;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.qa.base.BaseTest;
import com.qa.client.RestClient;

/**
 * GET API Test Class Tests GET endpoints to retrieve user data
 */
public class GetAPITest extends BaseTest {

	private static final Logger logger = Logger.getLogger(GetAPITest.class);

	String serviceURL;
	String apiURL;
	RestClient restClient;
	String url;
	CloseableHttpResponse closeableHttpResponse;

	@BeforeMethod
	public void setUp() {
		try {
			logger.info("========== Starting GET API Test Setup ==========");

			serviceURL = prop.getProperty("baseURL");
			apiURL = prop.getProperty("resourcePath");
			url = serviceURL + apiURL;

			logger.info("API URL: " + url);

			restClient = new RestClient();
			logger.info("RestClient initialized");

			logger.info("========== GET API Test Setup Completed ==========");
		} catch (Exception e) {
			logger.error("Setup failed: " + e.getMessage(), e);
			throw new RuntimeException("Setup failed: " + e.getMessage());
		}
	}

	/**
	 * Test GET API - Get user by ID
	 */
	@Test(priority = 1, description = "Get user by ID")
	public void getUserByIdTest() {
		try {
			logger.info("========== Starting Get User By ID Test ==========");

			extentTest = extent.createTest("GET API Test - Get User By ID");
			extentTest.log(Status.INFO, "Test started: Get user with ID 2");

			// Prepare headers
			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");
			logger.info("Headers prepared");
			extentTest.log(Status.INFO, "Headers configured");

			// Send GET request
			String getUserUrl = url + "/2"; // Get user with ID 2
			logger.info("Sending GET request to: " + getUserUrl);
			extentTest.log(Status.INFO, "GET request URL: " + getUserUrl);

			closeableHttpResponse = restClient.get(getUserUrl, headerMap);

			// Validate status code
			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);
			extentTest.log(Status.INFO, "Response status: " + statusCode);

			Assert.assertEquals(statusCode, BaseTest.RESPONSE_STATUS_CODE_200, "Expected status 200 for GET request");
			logger.info("✓ Status code validation passed");
			extentTest.log(Status.PASS, "Status validation passed");

			// Extract and parse response
			String responseString = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
			logger.info("Response: " + responseString);
			extentTest.log(Status.INFO, "Response received");

			// Parse JSON response
			JSONObject responseJson = new JSONObject(responseString);
			JSONObject dataObject = responseJson.getJSONObject("data");

			// Validate response fields
			Assert.assertNotNull(dataObject.get("id"), "ID should not be null");
			Assert.assertEquals(dataObject.getInt("id"), 2, "ID should be 2");
			Assert.assertNotNull(dataObject.get("email"), "Email should not be null");
			Assert.assertNotNull(dataObject.get("first_name"), "First name should not be null");

			logger.info("✓ All validations passed");
			logger.info("User details: " + dataObject.toString());
			extentTest.log(Status.PASS, "All validations passed");

			logger.info("========== Get User By ID Test Completed ==========");

		} catch (AssertionError e) {
			logger.error("Assertion failed: " + e.getMessage(), e);
			extentTest.log(Status.FAIL, "Test failed: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage(), e);
			extentTest.log(Status.FAIL, "Exception: " + e.getMessage());
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
	 * Test GET API - Get all users
	 */
	@Test(priority = 2, description = "Get all users")
	public void getAllUsersTest() {
		try {
			logger.info("========== Starting Get All Users Test ==========");

			extentTest = extent.createTest("GET API Test - Get All Users");
			extentTest.log(Status.INFO, "Test started: Get all users");

			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");

			logger.info("Sending GET request to: " + url);
			extentTest.log(Status.INFO, "GET request URL: " + url);

			closeableHttpResponse = restClient.get(url, headerMap);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);

			Assert.assertEquals(statusCode, 200);
			extentTest.log(Status.PASS, "Status code validation passed");

			String responseString = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");
			logger.info("Response received");

			JSONObject responseJson = new JSONObject(responseString);
			JSONArray dataArray = responseJson.getJSONArray("data");

			Assert.assertTrue(dataArray.length() > 0, "Users list should not be empty");
			logger.info("Total users found: " + dataArray.length());
			extentTest.log(Status.PASS, "Found " + dataArray.length() + " users");

			logger.info("========== Get All Users Test Completed ==========");

		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage(), e);
			extentTest.log(Status.FAIL, "Exception: " + e.getMessage());
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
	 * Test GET API for non-existent user Negative test
	 */
	@Test(priority = 3, description = "Get non-existent user - Negative Test")
	public void getNonExistentUserTest() {
		try {
			logger.info("========== Starting Non-Existent User Test ==========");

			extentTest = extent.createTest("GET API Test - Non-Existent User (Negative)");
			extentTest.log(Status.INFO, "Test started: Get non-existent user");

			HashMap<String, String> headerMap = new HashMap<>();
			headerMap.put("Content-Type", "application/json");

			String nonExistentUrl = url + "/999";
			logger.info("Requesting non-existent user: " + nonExistentUrl);
			extentTest.log(Status.INFO, "GET request to non-existent ID: 999");

			closeableHttpResponse = restClient.get(nonExistentUrl, headerMap);

			int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
			logger.info("Response status code: " + statusCode);

			Assert.assertEquals(statusCode, 404, "Should return 404 for non-existent user");
			logger.info("✓ Correctly returned 404");
			extentTest.log(Status.PASS, "API correctly returned 404");

			logger.info("========== Non-Existent User Test Completed ==========");

		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage(), e);
			extentTest.log(Status.FAIL, "Exception: " + e.getMessage());
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