package com.qa.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * BaseTest - Base class for all test classes Handles configuration, logging,
 * and reporting setup
 */
public class BaseTest {

	// Logger
	private static final Logger logger = Logger.getLogger(BaseTest.class);

	// Properties
	public Properties prop;

	// Extent Reports - FIXED: Made static and proper initialization
	public static ExtentReports extent;
	public static ExtentSparkReporter sparkReporter;

	// FIXED: Changed from static ThreadLocal to instance variable
	protected ExtentTest extentTest;

	// HTTP Status Code Constants
	public static final int RESPONSE_STATUS_CODE_200 = 200;
	public static final int RESPONSE_STATUS_CODE_201 = 201;
	public static final int RESPONSE_STATUS_CODE_204 = 204;
	public static final int RESPONSE_STATUS_CODE_400 = 400;
	public static final int RESPONSE_STATUS_CODE_401 = 401;
	public static final int RESPONSE_STATUS_CODE_403 = 403;
	public static final int RESPONSE_STATUS_CODE_404 = 404;
	public static final int RESPONSE_STATUS_CODE_500 = 500;
	public static final int RESPONSE_STATUS_CODE_503 = 503;

	/**
	 * BeforeSuite - Runs once before entire test suite
	 */
	@BeforeSuite(alwaysRun = true)
	public void setUp() {
		try {
			logger.info("========================================");
			logger.info("===== STARTING TEST SUITE SETUP ======");
			logger.info("========================================");

			// Load configuration
			loadConfiguration();

			// Configure Log4j
			configureLogging();

			// Setup Extent Reports
			setupExtentReports();

			logger.info("========================================");
			logger.info("===== TEST SUITE SETUP COMPLETED =====");
			logger.info("========================================");

		} catch (Exception e) {
			logger.error("CRITICAL ERROR in BeforeSuite setup: " + e.getMessage(), e);
			e.printStackTrace();
			throw new RuntimeException("Suite setup failed: " + e.getMessage(), e);
		}
	}

	/**
	 * Load configuration from properties file
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

			logger.info("Configuration loaded successfully");
			logger.info("Base URL: " + prop.getProperty("baseURL"));
			logger.info("Environment: " + prop.getProperty("environment", "test"));

		} catch (IOException e) {
			logger.error("Failed to load configuration: " + e.getMessage(), e);
			throw new IOException("Configuration loading failed", e);
		}
	}

	@BeforeMethod
	public void setup(ITestContext context) {
		String env = context.getCurrentXmlTest().getParameter("environment");
	}

	/**
	 * Configure Log4j logging
	 */
	private void configureLogging() {
		try {
			logger.info("Configuring Log4j...");

			String log4jConfigFile = System.getProperty("user.dir") + "/src/main/resources/log4j.properties";

			PropertyConfigurator.configure(log4jConfigFile);

			logger.info("Log4j configured successfully");

		} catch (Exception e) {
			logger.error("Failed to configure Log4j: " + e.getMessage(), e);
		}
	}

	/**
	 * Setup Extent Reports
	 */
	private void setupExtentReports() {
		try {
			logger.info("Setting up Extent Reports...");

			String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
			String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport_" + timestamp + ".html";

			sparkReporter = new ExtentSparkReporter(reportPath);
			sparkReporter.config().setDocumentTitle("REST API Test Automation Report");
			sparkReporter.config().setReportName("API Test Execution Report");
			sparkReporter.config().setTheme(Theme.STANDARD);
			sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

			extent = new ExtentReports();
			extent.attachReporter(sparkReporter);

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
			e.printStackTrace();
		}
	}

	/**
	 * AfterMethod - Runs after each test method
	 */
	@AfterMethod(alwaysRun = true)
	public void afterMethod(ITestResult result) {
		try {
			// FIXED: Use instance variable instead of ThreadLocal
			if (extentTest != null) {
				String testName = result.getMethod().getMethodName();

				if (result.getStatus() == ITestResult.SUCCESS) {
					logger.info("✓ TEST PASSED: " + testName);
					extentTest.log(Status.PASS, MarkupHelper.createLabel("TEST PASSED", ExtentColor.GREEN));

				} else if (result.getStatus() == ITestResult.FAILURE) {
					logger.error("✗ TEST FAILED: " + testName);
					logger.error("Failure reason: " + result.getThrowable().getMessage());

					extentTest.log(Status.FAIL, MarkupHelper.createLabel("TEST FAILED", ExtentColor.RED));
					extentTest.fail("Test Failed: " + result.getThrowable().getMessage());
					extentTest.fail(result.getThrowable());

				} else if (result.getStatus() == ITestResult.SKIP) {
					logger.warn("⊗ TEST SKIPPED: " + testName);
					extentTest.log(Status.SKIP, MarkupHelper.createLabel("TEST SKIPPED", ExtentColor.YELLOW));
					extentTest.skip("Test Skipped: " + result.getThrowable());
				}

				logger.info("Test execution time: " + (result.getEndMillis() - result.getStartMillis()) + " ms");
			}

		} catch (Exception e) {
			logger.error("Error in AfterMethod: " + e.getMessage(), e);
		}
	}

	/**
	 * AfterSuite - Runs once after entire test suite
	 */
	@AfterSuite(alwaysRun = true)
	public void tearDown() {
		try {
			logger.info("========================================");
			logger.info("===== TEST SUITE TEAR DOWN ==========");
			logger.info("========================================");

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
	 * Get property value
	 */
	public String getProperty(String key) {
		return prop.getProperty(key);
	}

	/**
	 * Get property with default value
	 */
	public String getProperty(String key, String defaultValue) {
		return prop.getProperty(key, defaultValue);
	}
}