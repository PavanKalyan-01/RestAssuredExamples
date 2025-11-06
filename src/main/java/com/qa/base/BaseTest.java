package com.qa.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Base Test Class Contains common setup, configuration, and utilities for all
 * test classes
 * 
 * Features: - Properties file loading - Log4j configuration - Extent Reports
 * setup - HTTP status code constants - Common test lifecycle methods
 */
public class BaseTest {

	// ============ LOGGER ============
	private static final Logger logger = Logger.getLogger(BaseTest.class);

	// ============ CONFIGURATION ============
	/**
	 * Properties object to read configuration from properties file Contains:
	 * baseURL, resourcePath, environment, etc.
	 */
	public Properties prop;

	// ============ EXTENT REPORTS ============
	/**
	 * ExtentReports - Main reporting object Generates HTML test reports with charts
	 * and logs
	 */
	public static ExtentReports extent;

	/**
	 * ExtentSparkReporter - Report renderer Creates the actual HTML file
	 */
	public static ExtentSparkReporter sparkReporter;

	/**
	 * ExtentTest - Individual test instance Each test method gets its own
	 * ExtentTest object Thread-safe for parallel execution
	 */
	//public static ExtentTest test = new ThreadLocal<>();
	public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
	// And always use test.get() inside your test methods.

	// And always use test.get() inside your test methods.

	// And always use test.get() inside your test methods.

	// ============ HTTP STATUS CODES ============
	/**
	 * Standard HTTP status code constants Used for response validation
	 */
	// 2xx Success codes
	public static final int RESPONSE_STATUS_CODE_200 = 200; // OK
	public static final int RESPONSE_STATUS_CODE_201 = 201; // Created
	public static final int RESPONSE_STATUS_CODE_204 = 204; // No Content

	// 4xx Client Error codes
	public static final int RESPONSE_STATUS_CODE_400 = 400; // Bad Request
	public static final int RESPONSE_STATUS_CODE_401 = 401; // Unauthorized
	public static final int RESPONSE_STATUS_CODE_403 = 403; // Forbidden
	public static final int RESPONSE_STATUS_CODE_404 = 404; // Not Found

	// 5xx Server Error codes
	public static final int RESPONSE_STATUS_CODE_500 = 500; // Internal Server Error
	public static final int RESPONSE_STATUS_CODE_503 = 503; // Service Unavailable

	/**
	 * BeforeSuite - Runs once before entire test suite
	 * 
	 * Responsibilities: 1. Load configuration properties 2. Configure Log4j logging
	 * 3. Setup Extent Reports
	 * 
	 * @throws IOException if config files not found
	 */
	@BeforeSuite(alwaysRun = true)
	public void setUp() {
		try {
			logger.info("========================================");
			logger.info("===== STARTING TEST SUITE SETUP ======");
			logger.info("========================================");

			// STEP 1: Load configuration properties
			loadConfiguration();

			// STEP 2: Configure Log4j
			configureLogging();

			// STEP 3: Setup Extent Reports
			setupExtentReports();

			logger.info("========================================");
			logger.info("===== TEST SUITE SETUP COMPLETED =====");
			logger.info("========================================");

		} catch (Exception e) {
			logger.error("CRITICAL ERROR in BeforeSuite setup: " + e.getMessage(), e);
			throw new RuntimeException("Suite setup failed: " + e.getMessage(), e);
		}
	}

	/**
	 * Load configuration from properties file
	 * 
	 * Properties file location: src/main/java/com/qa/config/config.properties
	 * Contains: baseURL, resourcePath, timeout, etc.
	 * 
	 * @throws IOException if properties file not found
	 */
	private void loadConfiguration() throws IOException {
		try {
			logger.info("Loading configuration properties...");

			prop = new Properties();
			String configPath = System.getProperty("user.dir") + "/src/main/java/com/qa/config/config.properties";

			logger.info("Config file path: " + configPath);

			FileInputStream fis = new FileInputStream(configPath);
			prop.load(fis);
			fis.close();

			// Log loaded properties
			logger.info("Configuration loaded successfully");
			logger.info("Base URL: " + prop.getProperty("baseURL"));
			logger.info("Environment: " + prop.getProperty("environment", "test"));

		} catch (IOException e) {
			logger.error("Failed to load configuration: " + e.getMessage(), e);
			throw new IOException("Configuration loading failed", e);
		}
	}

	/**
	 * Configure Log4j logging framework
	 * 
	 * Log4j properties file: src/main/resources/log4j.properties Configures log
	 * levels, appenders, and log file location
	 */
	private void configureLogging() {
		try {
			logger.info("Configuring Log4j...");

			String log4jConfigFile = System.getProperty("user.dir") + "/src/main/resources/log4j.properties";

			PropertyConfigurator.configure(log4jConfigFile);

			logger.info("Log4j configured successfully");
			logger.info("Log file location: logs/application.log");

		} catch (Exception e) {
			logger.error("Failed to configure Log4j: " + e.getMessage(), e);
			// Don't throw exception, logging is not critical for test execution
		}
	}

	/**
	 * Setup Extent Reports
	 * 
	 * Creates HTML report with: - Test execution dashboard - Pie charts for
	 * pass/fail - Detailed test logs - Screenshots (if added) - System information
	 */
	private void setupExtentReports() {
		try {
			logger.info("Setting up Extent Reports...");

			// Generate unique report name with timestamp
			String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport_" + timestamp + ".html";

			// Create SparkReporter (HTML renderer)
			sparkReporter = new ExtentSparkReporter(reportPath);

			// Configure report settings
			sparkReporter.config().setDocumentTitle("REST API Test Automation Report");
			sparkReporter.config().setReportName("API Test Execution Report");
			sparkReporter.config().setTheme(Theme.STANDARD); // or Theme.DARK
			sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

			// Create ExtentReports object
			extent = new ExtentReports();
			extent.attachReporter(sparkReporter);

			// Add system/environment information
			extent.setSystemInfo("Application", "REST API Testing Framework");
			extent.setSystemInfo("Operating System", System.getProperty("os.name"));
			extent.setSystemInfo("User Name", System.getProperty("user.name"));
			extent.setSystemInfo("Java Version", System.getProperty("java.version"));
			extent.setSystemInfo("Environment", prop.getProperty("environment", "Test"));
			extent.setSystemInfo("Base URL", prop.getProperty("baseURL"));

			logger.info("Extent Reports configured successfully");
			logger.info("Report will be generated at: " + reportPath);

		} catch (Exception e) {
			logger.error("Failed to setup Extent Reports: " + e.getMessage(), e);
			// Don't throw exception, reporting is not critical for test execution
		}
	}

	/**
	 * AfterMethod - Runs after each @Test method
	 * 
	 * Purpose: - Log test results to Extent Report - Capture pass/fail status - Add
	 * error details if test failed
	 * 
	 * @param result - TestNG test result object
	 */
	@AfterMethod(alwaysRun = true)
	public void afterMethod(ITestResult result) {
		try {
			ExtentTest extentTest = test.get();

			if (extentTest != null) {
				String testName = result.getMethod().getMethodName();

				// Check test status and log accordingly
				if (result.getStatus() == ITestResult.SUCCESS) {
					// Test passed
					logger.info("✓ TEST PASSED: " + testName);
					extentTest.log(Status.PASS, MarkupHelper.createLabel("TEST PASSED", ExtentColor.GREEN));

				} else if (result.getStatus() == ITestResult.FAILURE) {
					// Test failed
					logger.error("✗ TEST FAILED: " + testName);
					logger.error("Failure reason: " + result.getThrowable().getMessage());

					extentTest.log(Status.FAIL, MarkupHelper.createLabel("TEST FAILED", ExtentColor.RED));
					extentTest.fail("Test Failed: " + result.getThrowable().getMessage());
					extentTest.fail(result.getThrowable());

				} else if (result.getStatus() == ITestResult.SKIP) {
					// Test skipped
					logger.warn("⊗ TEST SKIPPED: " + testName);
					extentTest.log(Status.SKIP, MarkupHelper.createLabel("TEST SKIPPED", ExtentColor.YELLOW));
					extentTest.skip("Test Skipped: " + result.getThrowable().getMessage());
				}

				logger.info("Test execution time: " + (result.getEndMillis() - result.getStartMillis()) + " ms");
			}

		} catch (Exception e) {
			logger.error("Error in AfterMethod: " + e.getMessage(), e);
		}
	}

	/**
	 * AfterSuite - Runs once after entire test suite
	 * 
	 * Purpose: - Flush and finalize Extent Report - Generate final HTML report -
	 * Log suite completion
	 */
	@AfterSuite(alwaysRun = true)
	public void tearDown() {
		try {
			logger.info("========================================");
			logger.info("===== TEST SUITE TEAR DOWN ==========");
			logger.info("========================================");

			// Flush Extent Reports (write to file)
			if (extent != null) {
				extent.flush();
				logger.info("Extent Report generated successfully");
			}

			logger.info("========================================");
			logger.info("===== TEST SUITE COMPLETED ==========");
			logger.info("========================================");

		} catch (Exception e) {
			logger.error("Error in AfterSuite: " + e.getMessage(), e);
		}
	}

	/**
	 * Utility method to get property value
	 * 
	 * @param key - Property key
	 * @return Property value or null
	 */
	public String getProperty(String key) {
		return prop.getProperty(key);
	}

	/**
	 * Utility method to get property with default value
	 * 
	 * @param key          - Property key
	 * @param defaultValue - Default value if key not found
	 * @return Property value or default value
	 */
	public String getProperty(String key, String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}

	/**
	 * Get current test instance (thread-safe)
	 * 
	 * @return Current ExtentTest instance
	 */
	public ExtentTest getTest() {
		return test.get();
	}
}