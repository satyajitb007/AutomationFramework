package com.framework.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public final class Util {
	public static boolean deleteFlag=false;
	public Properties prop;
	public static String projectPath = Paths.get("").toAbsolutePath().toString();
	public ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent;
	static String browser = null;
	static Boolean dubugger = null;
	// Removed unused Selenium driver field for Playwright migration
	// private static WebDriver driver;
	public static String XMLSUITE_NAME = null;
	File reportBackupDir;
	File rootDir;
	private Util() {}
	private static Util instance=new Util();
	private static final List<Status> STATUS_HIERARCHY = Arrays.asList(Status.FATAL, Status.FAIL, Status.ERROR,
			Status.SKIP, Status.PASS, Status.INFO, Status.WARNING, Status.DEBUG);
	/**
	 * This method is used to get the singleton instance of the Util class. The Util
	 * class is a singleton, which means that there is only one instance of it
	 * throughout the application. This method provides a global point of access to
	 * the Util instance.
	 *
	 * @return Util singleton instance
	 * @author Satyajit
	 */
	public static Util getInstance() {
		return instance;
	}

	/**
	 * This method is used to get the Properties object. The Properties class
	 * represents a persistent set of properties which can be saved to a stream or
	 * loaded from a stream. This method provides a global point of access to the
	 * Properties object.
	 *
	 * @return Properties object
	 * @author Satyajit
	 */
	public Properties GetPropertyObject() {
		return prop;
	}
	/**
	 * This method creates and loads properties files for configuration and API settings.
	 * It checks if the files exist, creates them if they do not, and then loads the properties from the files.
	 * If an IOException occurs during the process, it is caught and the stack trace is printed.
	 */
	public void CreatePropertyfile() {
	    try {
	        // Initialize the properties object for configuration
	        prop = new Properties();
	        Path configFilePath = Paths.get("").toAbsolutePath().resolve("configuration/config.properties");

	        // Check if the configuration file exists, create it if it does not
	        if (Files.notExists(configFilePath)) {
	            Files.createFile(configFilePath);
	        }

	        // Load the properties from the configuration file
	        try (FileInputStream in = new FileInputStream(configFilePath.toFile())) {
	            prop.load(in);
	        }

	        // Load all other properties files in the configuration folder
	        File configDir = new File(Paths.get("").toAbsolutePath().resolve("configuration").toString());
	        File[] files = configDir.listFiles((dir, name) -> name.endsWith(".properties") && !name.equals("config.properties"));

	        if (files != null) {
	            for (File file : files) {
	                Properties tempProp = new Properties();
	                try (FileInputStream in = new FileInputStream(file)) {
	                    tempProp.load(in);
	                }
	                String fileName = file.getName().replace(".properties", "");
	                for (String key : tempProp.stringPropertyNames()) {
	                    prop.setProperty(fileName + "." + key, tempProp.getProperty(key));
	                }
	            }
	        }
	    } catch (IOException e) {
	        // Print the stack trace if an IOException occurs
	        e.printStackTrace();
	    }
	}
	/**
	 * This method is used to decrypt a Base64 encoded string. The Base64 class is a
	 * part of the java.util package and provides a method to decode a Base64
	 * encoded string. This method takes a Base64 encoded string as input, decodes
	 * it and returns the original string.
	 *
	 * @param text The Base64 encoded string to be decrypted
	 * @return The original string after decryption
	 * @throws IllegalArgumentException if the input text is not a valid Base64
	 *                                  encoded string
	 * @author Satyajit
	 */
	public static String decryptString(String text) throws IllegalArgumentException {
		byte[] decodedBytes = Base64.getDecoder().decode(text.getBytes());
		return new String(decodedBytes);
	}

	/**
	 * encrypt the string
	 *
	 * @name encryptString
	 * @description This method will encrypt the string pass to this function
	 * @param text ||description: String to be encrypted ||allowedRange:
	 * @return NA
	 * @jiraId
	 * @author Satyajit
	 * 
	 */
	public static String encryptString(String text) throws IllegalArgumentException {
		byte[] encodeBytes = Base64.getEncoder().encode(text.getBytes());
		return new String(encodeBytes);
	}
	
	
	
	/**
	 * Copy directory from one location to another
	 *
	 * @name copyDirectory
	 * @description Copy directory from one location to another
	 * @author Vaibhav Narkhede
	 * @param sourceDirectoryLocation ||description: source dir location ||allowedRange:
	 * @param destinationDirectoryLocation ||description: destination dir location ||allowedRange:
	 * @return NA
	 * @jiraId
	 */
	
	public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
	    File sourceDirectory = new File(sourceDirectoryLocation);
	    File destinationDirectory = new File(destinationDirectoryLocation);
	    FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
	}

	/**
	 * This method is used to initialize the Extent Reports for logging the test
	 * execution details. It creates the necessary directories for storing the
	 * reports, sets up the HTML reporter, and attaches it to the Extent Reports. It
	 * also sets various system information in the report such as Operating System,
	 * Browser, Environment, Testing Type, and Executed By. It also handles the
	 * backup of the previous report by creating a backup directory and copying the
	 * old report into it. The old report and snapshot directory are then cleaned
	 * up.
	 *
	 * @param prop The Properties object containing the configuration details
	 * @return The initialized ExtentReports object
	 * @throws Exception if any error occurs during the initialization process
	 * @author Satyajit
	 */
	public ExtentReports initExtentReport(Properties prop) {
		// extent report
		try {
			// Backup directory
			String backUpDirPath = "C:\\TestReportBackup"; // use prop object

			// creating report directory
			rootDir = new File(projectPath + "\\test-output\\extent-report");
			rootDir.mkdirs();

			// Create Backup report directory
			File backupDir = new File(backUpDirPath);
			backupDir.mkdir();

			// Create Backup Subdirectory
			DateFormat dirDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH-mm-ss");
			Date date = new Date();
			reportBackupDir = new File(backUpDirPath + "\\" + dirDateFormat.format(date));
			reportBackupDir.mkdir();

			// Take backup
			Util.copyDirectory(projectPath + "\\test-output\\extent-report", reportBackupDir.getAbsolutePath());

			// clean the extent report directory and retain snapshot folder
			for (File file : rootDir.listFiles()) {
				if (!file.isDirectory())
					file.delete();
			}

			// clean snapshot directory
			File rootSnapDir = new File(projectPath + "\\test-output\\extent-report\\snapshot");
			if (rootSnapDir.exists()) { // check snapshot directory exist
				for (File file : rootSnapDir.listFiles()) {
					file.delete();
				}
			}

			// Initialize HTML reporter
			htmlReporter = new ExtentHtmlReporter(
					rootDir.getAbsolutePath() + "\\" + Paths.get(projectPath).getFileName() + "_TestResult.html");
			htmlReporter.loadXMLConfig(projectPath + "\\configuration\\html-config.xml");

			// Set document title and report name if XMLSUITE_NAME is present
			Optional.ofNullable(XMLSUITE_NAME).ifPresent(name -> {
				htmlReporter.config().setDocumentTitle(name);
				htmlReporter.config().setReportName("ABS-AUTOMATION REPORT:" + name);
			});

			// Initialize ExtentReports and attach the HTML reporter
			extent = new ExtentReports();
			setExtentReportStatusHierarchy();
			extent.attachReporter(htmlReporter);
			extent.setSystemInfo("Operating System", System.getProperty("os.name").toUpperCase());
			extent.setSystemInfo("Browser", prop.getProperty("env.browser").toUpperCase());

			// Set environment and testing type
			String environment = Optional.ofNullable(System.getProperty("env.environment")).map(String::toUpperCase)
					.orElse(prop.getProperty("env.environment"));
			extent.setSystemInfo("Environment", environment);
			String feature = Optional.ofNullable(System.getProperty("testingType")).map(String::toUpperCase)
					.orElse(prop.getProperty("env.feature"));
			extent.setSystemInfo("Testing Type", feature);

			// Set executed by
			extent.setSystemInfo("Executed By", System.getProperty("user.name").toUpperCase());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return extent;
	}

	/**
	 * This method sets the status hierarchy for the ExtentReports instance. The
	 * status hierarchy is a list of Status enumeration values that determine the
	 * order of statuses in the report. The order of statuses in the list determines
	 * their priority, with earlier statuses having higher priority. This method is
	 * used to customize the status hierarchy according to the requirements of the
	 * report.
	 *
	 * @author Satyajit
	 */
	public void setExtentReportStatusHierarchy() {
		extent.config().statusConfigurator().setStatusHierarchy(STATUS_HIERARCHY);
	}
}