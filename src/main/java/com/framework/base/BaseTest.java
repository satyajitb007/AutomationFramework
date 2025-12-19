package com.framework.base;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Optional;
import java.util.Properties;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.framework.lib.BrowserManager;
import com.framework.lib.DatabaseConnection;
import com.framework.lib.ExtentReportMGR;
import com.framework.lib.Util;
import com.framework.logger.FrameworkServiceManager;
import com.framework.logger.RetryAnalyzerTestNG;

public class BaseTest extends FrameworkServiceManager {
    public static Properties prop;
    public static Connection con;
    public ExtentHtmlReporter htmlReporter;
    protected static ExtentReports extent;
    protected static ThreadLocal<ExtentTest> logger = new ThreadLocal<>();
    protected static ThreadLocal<ExtentTest> step = new ThreadLocal<>();
    public static String projectPath = Paths.get("").toAbsolutePath().toString();
    File reportBackupDir;
    File rootDir;

	/**
	 * This method is annotated with @BeforeSuite, which means it will be executed
	 * before any tests in the suite are run. It initializes the property file by
	 * calling the CreatePropertyfile method from the Util class. It also sets up
	 * various timeout values by reading them from the property file.
	 * @author Satyajit
	 */
	@BeforeSuite()
	public void initiatePropertyFile() {
		Util.getInstance().CreatePropertyfile();
		prop = Util.getInstance().prop;
		Util.deleteFlag = true;
		int baseTimeout;
		try {
		    baseTimeout = Integer.parseInt(prop.getProperty("DEFAULT_TIMEOUT"));
		    if (baseTimeout < 0) baseTimeout = 30000;
		} catch (Exception e) {
		    baseTimeout = 30000;
		}

		DEFAULT_TIMEOUT = baseTimeout;//60000--60 sec
		QUICK_TIMEOUT = Math.max(1, (baseTimeout + 9) / 10);//6000-- 6 sec
		MEDIUM_TIMEOUT = (baseTimeout + 1) / 2;//30000--30 sec
		LONG_TIMEOUT = (baseTimeout <= Integer.MAX_VALUE / 2) ? baseTimeout * 2 : Integer.MAX_VALUE;//120000--2 min
		EXTRA_LONG_TIMEOUT = (baseTimeout <= Integer.MAX_VALUE / 4) ? baseTimeout * 4 : Integer.MAX_VALUE;//240000--4 min
	}

	/**
	 * This method is annotated with @BeforeSuite and depends on the
	 * "initiatePropertyFile" method. It sets up the ExtentReports instance for the
	 * test suite. ExtentReports is a reporting library for automation testing which
	 * creates interactive and detailed logs of tests. It first gets the singleton
	 * instance of ExtentReportMGR and sets the ExtentReports instance. Then it
	 * retrieves the ExtentReports instance and assigns it to the local extent
	 * variable.
	 */
	@BeforeSuite(dependsOnMethods = { "initiatePropertyFile" })
	public void reportSetup() {
		ExtentReportMGR.getInstance().setExtentReports(Util.getInstance().initExtentReport(prop));
		extent = ExtentReportMGR.getInstance().getExtentReports();

	}

	/**
	 * This method is annotated with @BeforeMethod, which means it will be executed
	 * before each test method. It initializes the WebDriver instance and creates a
	 * new ScreenShots object. The ScreenShots object is used to take screenshots
	 * during the test, which can be useful for debugging. The screenshots are saved
	 * in the "test-output/extent-report" directory.
	 */
	@BeforeClass
	public void setUp() {
	    try {
	        RetryAnalyzerTestNG.getInstance().setRetryCount(0);
	        String testName = this.getClass().getSimpleName();
	        BrowserManager.getInstance().setDownloadfolder(projectPath + "\\downloads\\" + testName);
	        BrowserManager.getInstance().setTestName(testName);
	        // Launch Playwright browser (default to Chromium, or use property)
	        String browserType = Optional.ofNullable(prop.getProperty("env.browser")).orElse("chromium");
	        BrowserManager.getInstance().launchBrowser(browserType);
	        
	    } catch (Exception e) {
	    }
	}
	/**
	 * Flushes the ExtentReports instance after each test method. This ensures that
	 * all the logs and information related to the test method are written to the
	 * report file.
	 * 
	 * @throws IOException if an I/O error occurs while flushing the report
	 * @author Satyajit
	 */
	@AfterClass
	public void clear() {
		ExtentReportMGR.getInstance().removeExtentTest();
	}

	/**
	 * Get the current test method name properly
	 */
	/**
	 * This method is annotated with @AfterSuite, which means it will be executed
	 * after all the tests in the suite have run. It performs cleanup activities
	 * that are necessary after the entire test suite, such as closing the browser.
	 * It also opens the test report in the default desktop browser.
	 *
	 * @throws IOException if an I/O error occurs while opening the test report
	 */
	@AfterSuite
	public void closeBrowser() throws IOException {
		try {
			ExtentReportMGR.getInstance().getExtentReports().flush();
			DatabaseConnection.closeConnection();
			// Open the test report in the default desktop browser.
			openReport();
		} catch (IOException e) {
		}
	}

	public void openReport() throws IOException {
		Optional.ofNullable(prop.getProperty("env.openReport")).map(Boolean::parseBoolean).ifPresent(isOpen -> {
			if (isOpen) {
				try {
					Desktop.getDesktop().open(new File(projectPath + "\\test-output\\extent-report\\"
							+ Paths.get(projectPath).getFileName() + "_TestResult.html"));
					// Desktop.getDesktop().open(new File(projectPath +
					// "\\test-output\\extent-report\\ApplicationReport.html"));

				} catch (IOException e) {
					exception(e);
				}
			}
		});

	}

	/**
	 * This method assigns a title to the Extent Report for the current test method.
	 * It retrieves the method name and description from the TestNG annotations and
	 * uses them to create a new test in the Extent Report. The title is set to the
	 * description if it is available, otherwise it defaults to the method name. The
	 * created ExtentTest instance is then set in the ExtentReportMGR for further
	 * logging.
	 * 
	 * @author Satyajit
	 * @return void
	 */
	public void assignTestTitleToExtentReport(String reportTitle, String...ticketID) {
		String jiraID = (ticketID.length > 0 && ticketID[0] != null && !ticketID[0].isEmpty())
			    ? String.format(" [<a href='https://americanbureauofshipping.atlassian.net/browse/%s' target='_blank'>%s</a>]", ticketID[0], ticketID[0])
			    : "";
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		ExtentTest test = ExtentReportMGR.getInstance().getExtentReports().createTest(reportTitle, methodName+":"+jiraID);
		//logger.set(test);
		step.set(test);
		ExtentReportMGR.getInstance().setExtentTest(test);
		
		// Add category assignment if feature is available
		try {
			String feature = Optional.ofNullable(System.getProperty("testingType")).map(String::toUpperCase)
					.orElse(prop.getProperty("env.feature"));
			if (feature != null && !feature.isEmpty()) {
				test.assignCategory(feature);
			}
		} catch (Exception e) {
			System.out.println("Failed to assign category: " + e.getMessage());
		}
	}	
	public void assignTestTitleToExtentReport() {
	    String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
	    String testDescription = "";
	    try {
	        testDescription = this.getClass().getMethod(methodName).getAnnotation(org.testng.annotations.Test.class)
	                .description();
	    } catch (Exception e) {
	        // handle exception if needed
	    }
	    String reportTitle = (testDescription != null && !testDescription.isEmpty()) ? testDescription : methodName;
	    ExtentTest test = ExtentReportMGR.getInstance().getExtentReports().createTest(reportTitle, methodName);
	    //logger.set(test);
	    step.set(test);
	    ExtentReportMGR.getInstance().setExtentTest(test);
	    
	    // Add category assignment if feature is available
	    try {
	        String feature = Optional.ofNullable(System.getProperty("testingType")).map(String::toUpperCase)
	                .orElse(prop.getProperty("env.feature"));
	        if (feature != null && !feature.isEmpty()) {
	            test.assignCategory(feature);
	        }
	    } catch (Exception e) {
	        System.out.println("Failed to assign category: " + e.getMessage());
	    }
	}

	/**
	 * This method creates a new step in the Extent Report for the current test. It
	 * retrieves the current ExtentTest instance from the ThreadLocal variable and
	 * creates a new node with the provided step name. The new node is then set as
	 * the current ExtentTest instance in the ThreadLocal variable for further
	 * logging.
	 * 
	 * @author Satyajit
	 * @param stepName The name of the step to be created in the report.
	 * @return void
	 */
	public void createStep(String stepName) {
		ExtentReportMGR.getInstance().setExtentTestNode(step.get(), stepName);
		//logger.set(node);
	}
}
