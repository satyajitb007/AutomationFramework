package com.framework.base;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.testng.SkipException;

import com.framework.lib.BrowserManager;
import com.framework.lib.DatabaseConnection;
import com.framework.lib.ExtentReportMGR;
import com.framework.lib.Util;
import com.framework.logger.FrameworkServiceManager;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.warrenstrange.googleauth.GoogleAuthenticator;

public class BasePage extends FrameworkServiceManager {
	BaseTest baseTest;

	// public static String[] DOWNLOADFILES;

	static String projectPath = Paths.get("").toAbsolutePath().toString();
	public static Duration timeout;

	protected int defaultRetryAttempts = 3;
	protected Duration defaultPollInterval = Duration.ofMillis(200);

	Properties prop = BaseTest.prop;

	public BasePage() {
		timeout = Duration.ofMillis(LONG_TIMEOUT);
	}

	// ******************************************
	/**
	 * isElementEnable
	 *
	 * @name isElementEnable
	 * @description Verifies if element is enabled
	 * @author Bijan Mallick
	 * @param element ||description: Element ||allowedRange:
	 * @return boolean ||description: true if element is enabled else false
	 * @jiraId
	 */
	public static boolean isElementEnable(Object element) {
		try {
			Locator locator;
			if (element instanceof Locator) {
				locator = (Locator) element;
			} else if (element instanceof String) {
				locator = BrowserManager.getInstance().getPage().locator(element.toString());
			} else {
				throw new IllegalArgumentException("Parameter must be either Locator or String");
			}
			return locator.isEnabled();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Wait for M seconds.
	 *
	 * @name waitForMSeconds
	 * @description Waits the thread for set milliseconds
	 * @author Vaibhav Narkhede
	 * @param timeoutInMilliSeconds ||description: Wait timeout in milliseconds
	 *                              ||allowedRange:
	 * @return void ||description:
	 * @throws InterruptedException
	 * @jiraId
	 */
	public static void waitForMSeconds(int timeoutInMilliSeconds) {
		try {
			BrowserManager.getInstance().getPage().waitForTimeout(timeoutInMilliSeconds);
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e.getMessage());
		}
	}

	/**
	 * Wait for element visible.
	 *
	 * @name waitForElementVisible
	 * @description Waits for the element to be visible on the page
	 * @author Bijan Mallick
	 * @param selector         ||description: Element's Locator ||allowedRange:
	 * @param timeoutInSeconds ||description: Wait timeout in seconds
	 *                         ||allowedRange:
	 * @return boolean ||description: true if element is visible else false
	 * @jiraId
	 */
	public static boolean waitForElementVisible(String selector, int timeoutInMS) {
		try {
			waitForMSeconds(1000);
			Page page = BrowserManager.getInstance().getPage();
			Locator locator = page.locator(selector);
			locator.waitFor(
					new Locator.WaitForOptions().setTimeout(timeoutInMS).setState(WaitForSelectorState.VISIBLE));
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * Scroll to bottom.
	 *
	 * @name scrollToBottom
	 * @description Scroll to the bottom of page
	 * @author Vaibhav Narkhede
	 * @return void ||description:
	 * @jiraId
	 */
	public static void scrollToBottom() throws Exception {
		try {

		} catch (Exception e) {
			if (e.getMessage().toLowerCase().contains("javascript error")) {
				new SkipException("JavaScriptExecutor's exception thrown:");
				// ExtentReportMGR.getInstance().getExtentTest()Manager.ExtentReportMGR.getInstance().getExtentTest().error(e.getMessage());
			} else
				ExtentReportMGR.getInstance().getExtentTest().error(e.getMessage());
		}
	}

	/**
	 * Scroll to top.
	 *
	 * @name scrollToTop
	 * @description Scroll to the top of page
	 * @author Vaibhav Narkhede
	 * @return void ||description:
	 * @jiraId
	 */
	public static void scrollToTop() {
		try {

		} catch (Exception e) {
			if (e.getMessage().toLowerCase().contains("javascript error")) {
				new SkipException("JavaScriptExecutor's exception thrown:");
				ExtentReportMGR.getInstance().getExtentTest().error(e);
			} else
				ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
	}

	/**
	 * click on webelement.
	 * 
	 * @name clickByAction
	 * @description click on web element using action class
	 * @author Bijan Mallick
	 * @param selector ||description: Element's Locator ||allowedRange:
	 * @return void ||description:
	 */

	public static void clickByAction(String selector) {
		int WaitTimeCounter = 0;
		Page page = BrowserManager.getInstance().getPage();
		Locator locator = page.locator(selector);
		do {
			try {
				locator.click(new Locator.ClickOptions().setForce(true));
				break;
			} catch (Exception e) {
				WaitTimeCounter = WaitTimeCounter + 1;
				try {
					Thread.sleep(500);
				} catch (Exception exp) {
				}
			}
		} while (WaitTimeCounter <= DEFAULT_TIMEOUT);
		if (WaitTimeCounter > DEFAULT_TIMEOUT) {
			throw new RuntimeException("The element '" + selector + "' is not enabled on the page to click.");
		}
	}

	/**
	 * click on webelement.
	 *
	 * @name click
	 * @description click on web element
	 * @author Bijan Mallick
	 * @param element ||description: Element ||allowedRange:
	 * @return void ||description:
	 */
	public boolean click(Object element, String... optionalElementName) {
		String printName = (optionalElementName.length > 0) ? optionalElementName[0] : null;
		try {
			Locator locator;
			if (element instanceof Locator) {
				locator = (Locator) element;
			} else if (element instanceof String) {
				locator = BrowserManager.getInstance().getPage().locator(element.toString());
			} else {
				throw new IllegalArgumentException("Parameter must be either Locator or String");
			}

			locator.waitFor(
					new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT).setState(WaitForSelectorState.VISIBLE));

			if (!locator.isEnabled()) {
				throw new RuntimeException("Element is not enabled.");
			}

			locator.evaluate("element => element.style.border = '2px solid red'");
			locator.click(new Locator.ClickOptions().setTimeout(DEFAULT_TIMEOUT));

			if (printName != null && !printName.trim().isEmpty()) {
				info("Clicked on " + printName);
			}
			return true;
		} catch (Exception e) {
			if (printName != null && !printName.trim().isEmpty()) {
				fail("Failed to click on " + printName);
				return false;
			} else {
				exception(e, false);
				return false;
			}
		}
	}

	/**
	 *
	 * @name clickByJavaScript
	 * @description click on web element using javascript
	 * @author Bijan Mallick
	 * @param element ||description: Element's Locator ||allowedRange:
	 * @return void ||description:
	 */

	public void clickByJavaScript(Object element) {
		try {
			Locator locator;

			if (element instanceof Locator) {
				locator = (Locator) element;
			} else if (element instanceof String) {
				// You'll need access to page instance here
				locator = BrowserManager.getInstance().getPage().locator((String) element);
			} else {
				throw new IllegalArgumentException("Parameter must be either Locator or String");
			}

			locator.evaluate("element => element.click()");
		} catch (Exception e) {
			exception(e);
		}
	}

	/**
	 * clear text field.
	 *
	 * @name clear
	 * @description clear text field
	 * @author Vaibhav Narkhede
	 * @param by ||description: Element's Locator ||allowedRange:
	 * @return
	 * @jiraId
	 */

	public static void clear(String selector) {
		Page page = BrowserManager.getInstance().getPage();
		Locator locator = page.locator(selector);
		locator.fill("");
	}

	/**
	 * Get text from element.
	 *
	 * @name getText
	 * @description Gets text from element
	 * @author Bijan Mallick
	 * @param element ||description: WebElement ||allowedRange:
	 * @return String ||description: text of element
	 * @jiraId
	 */
	public static String getText(String selector) {
		Page page = BrowserManager.getInstance().getPage();
		Locator locator = page.locator(selector);
		return locator.textContent().trim();
	}

	/**
	 * Select drop down by value.
	 *
	 * @name selectDropDownByValue
	 * @description Selects drop down element by value
	 * @author Vaibhav Narkhede
	 * @param element ||description: Drop down WebElement ||allowedRange:
	 * @param value   ||description: Value of element to be selected ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */
	public void selectDropDownByValue(String selector, String value) {
		try {
			Page page = BrowserManager.getInstance().getPage();
			Locator locator = page.locator(selector);
			locator.selectOption(value);
		} catch (Exception ex) {
			exception(ex);
		}
	}

	/**
	 * Select Drop Down By Visible Text.
	 *
	 * @name selectDropDownByVisibleText
	 * @description This method is used to select dropdown by visible text
	 * @author BalamuruganG
	 * @param element ||description: WebElement ||allowedRange:
	 * @param value   ||description: Select option ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */
	public static void selectDropDownByVisibleText(String selector, String value) {
		if (value == null || value.isEmpty())
			throw new IllegalArgumentException("String value to select is empty");
		Page page = BrowserManager.getInstance().getPage();
		Locator locator = page.locator(selector);
		locator.selectOption(value);
	}

	/*----------------------------------------------------------Frame-----------------------------------------------------------------------*/

	/**
	 * Switches to the frame and perform action
	 *
	 * @name switchToFramePerformAction
	 * @description Switches to the frame and perform action
	 * @author: Komal Verma
	 * @param frameIndex ||description: frameindex in which element is available
	 *                   ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */
	public static void switchToFrame(int frameIndex) {
		Page page = BrowserManager.getInstance().getPage();
		Frame frame = page.frames().get(frameIndex);
		frame.waitForLoadState();
	}

	/**
	 * Switches to the frame and perform action
	 *
	 * @name switchToFramePerformAction
	 * @description Switches to the frame and perform action
	 * @author: Vaibhav Narkhede
	 * @param frameName ||description: frame name in which element is available
	 *                  ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */
	public static void switchToFrame(String frameName) {
		Page page = BrowserManager.getInstance().getPage();
		for (Frame frame : page.frames()) {
			if (frame.name().equals(frameName)) {
				frame.waitForLoadState();
				break;
			}
		}
	}

	// Playwright migration: switchFromFrame is not needed; Playwright frames are
	// handled via Frame objects

	/*----------------------------------------------------------Others----------------------------------------------------------------------*/

	/**
	 * Clear input.
	 *
	 * @name clearInput
	 * @description clear input using BACKSPACE
	 * @author Vaibhav Narkhede
	 * @param element ||description: WebElement to be cleared ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */

	public static void clearInput(String selector) {
		try {
			Page page = BrowserManager.getInstance().getPage();
			Locator locator = page.locator(selector);
			locator.fill("");
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
	}

	/**
	 * Refresh page.
	 *
	 * @name refreshPage
	 * @description Refresh the WebPage
	 * @author Vaibhav Narkhede/Updated by BMallick on 3rdOct2025
	 * @return void ||description:
	 * @jiraId
	 */
	public void refreshPage() {
		Page page = BrowserManager.getInstance().getPage();
		try {
			info("Refreshing the current page");
			page.reload(new Page.ReloadOptions().setTimeout(LONG_TIMEOUT));
			page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(LONG_TIMEOUT));
			pass("Page refreshed successfully");
		} catch (Exception e) {
			error("Failed to refresh page: " + e.getMessage());
			throw new RuntimeException("Page refresh failed", e);
		}
	}

	/**
	 * Accept alert.
	 *
	 * @name acceptAlert
	 * @description Accepts an alert Message
	 * @author Vaibhav Narkhede
	 * @param sBtnText ||description: Button string value to be selected
	 *                 ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */
	public static void acceptAlert(String sBtnText) {
		try {
			Page page = BrowserManager.getInstance().getPage();
			page.onceDialog(dialog -> {
				switch (sBtnText) {
				case "Accept":
					dialog.accept();
					break;
				case "Dismiss":
					dialog.dismiss();
					break;
				default:
					dialog.accept();
				}
			});
		} catch (Exception ex) {
			ExtentReportMGR.getInstance().getExtentTest().error(ex);
		}
	}

	/**
	 * highLight the WebElement
	 *
	 * @name highLight
	 * @description Highlight the WebElement
	 * @author Vaibhav Narkhede
	 * @param webElement ||description: WebElement to be highlighted ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */

	public static void highLight(String selector) {
		try {
			Page page = BrowserManager.getInstance().getPage();
			Locator locator = page.locator(selector);
			locator.evaluate("element => element.style.border='3px solid red'");
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
	}

	/**
	 * highLight the WebElement in Green Color with Blue dotted line
	 *
	 * @name highLightBg
	 * @description Highlight the WebElement
	 * @author Vaibhav Narkhede
	 * @param by ||description: By class of Object of Xpath of WebElement to be
	 *           highlighted ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */

	public static void highlightBg(String selector) {
		try {
			Page page = BrowserManager.getInstance().getPage();
			Locator locator = page.locator(selector);
			locator.evaluate(
					"element => element.setAttribute('style', 'background: #00FF00; border: 4px dotted blue;')");
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
	}

	/**
	 * highLight the WebElement in specified input color
	 *
	 * @name highLightBg
	 * @description Highlight the WebElement
	 * @author Bijan Mallick
	 * @param by ||description: By class of Object of Xpath of WebElement to be,
	 *           color highlighted ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */

	public static void highlightBg(String selector, String hexCode) {
		try {
			Page page = BrowserManager.getInstance().getPage();
			Locator locator = page.locator(selector);
			locator.evaluate("(element, color) => element.setAttribute('style', 'background: ' + color + ';')",
					hexCode);
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
	}

	/*---------------------------------------------------------FILE OPERATION--------------------------------------------------*/

	/**
	 * It returns the latest downloaded file object in Downloads folder
	 *
	 * @name getLatestDownloadedFile
	 * @description It returns the latest downloaded file object in Downloads folder
	 * @author Bijan Mallick
	 * @param ||description: ||allowedRange:
	 * @return File ||description: File Object
	 * @jiraId
	 */

	public static File getLatestDownloadedFile() {
		try {
			String folderPath = BrowserManager.getInstance().getDownloadfolder();
			File dir = new File(folderPath);
			File[] files = dir.listFiles();
			if (files == null || files.length == 0) {
				ExtentReportMGR.getInstance().getExtentTest().warning("No File downloaded");
			}

			if (files == null || files.length == 0) {
				return null;
			}

			File lastModifiedFile = files[0];
			for (int i = 1; i < files.length; i++) {
				if (lastModifiedFile.lastModified() < files[i].lastModified()) {
					lastModifiedFile = files[i];
				}
			}

			return lastModifiedFile;

		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
		return null;
	}

	/**
	 * It deletes the file
	 *
	 * @name deleteFile
	 * @description It deletes the file
	 * @author Komal Verma
	 * @param file ||description: File Object ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */

	public static void deleteFile(File file) {

		try {
			if (file.exists())
				file.delete();

		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}

	}

	/**
	 * It creates the file and returns it's object
	 *
	 * @name createFile
	 * @description It creates the file and returns it's object
	 * @author Komal Verma
	 * @param fileName ||description: File Full path with name and extension
	 *                 ||allowedRange:
	 * @return File ||description: file object
	 * @jiraId
	 */

	public static File createFile(String fileName) {

		File file = new File(fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
		return file;
	}

	/**
	 * This will provide the last modified date of file
	 *
	 * @name getFileLastModifiedDate
	 * @description This will provide the last modified date of file based on the
	 *              format requested.
	 * @author Vaibhav Narkhede
	 * @param filePath ||description: File Full path with name and extension
	 *                 ||allowedRange:
	 * @param Format   ||description: date format in which expecting Modified date
	 *                 ||allowedRange:
	 * @return Last Modified Date ||description: file object
	 * @jiraId
	 */
	public static String getFileLastModifiedDate(String filePath, String Format) {

		SimpleDateFormat sdf = null;
		File file = null;

		try {

			file = new File(filePath);
			sdf = new SimpleDateFormat(Format);

		} catch (NullPointerException e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		} catch (IllegalArgumentException i) {
			ExtentReportMGR.getInstance().getExtentTest().error(i);
		}

		return sdf.format(file.lastModified());

	}

	/**
	 * getAllFilesInDownloadDirectory
	 *
	 * @name getAllFilesInDownloadDirectory
	 * @description This will return all file names from download directory
	 * @author Bijan Mallick
	 * @param clearDirectory ||description: optional boolean param if true it will
	 *                       clean entire directory ||allowedRange:
	 * @return String ||description: array of file names in String format
	 * @jiraId
	 */
	public static String[] getAllFilesInDownloadDirectory(boolean... clearDirectory) throws Exception {
		boolean clearFolder = false;
		if (clearDirectory.length > 0) {
			clearFolder = clearDirectory[0];
		}
		try {
			File downLoadDirectory = new File(BrowserManager.getInstance().getDownloadfolder());// Bijan on
																								// 19th May
																								// 2021
			if (clearFolder) {
				FileUtils.cleanDirectory(downLoadDirectory);
				ExtentReportMGR.getInstance().getExtentTest()
						.info("Cleared the Directory[" + downLoadDirectory.getAbsolutePath() + "]");
			}
			String[] DOWNLOADFILES = downLoadDirectory.list(new FilenameFilter() {
				public boolean accept(File dir, String fileName) {
					// if (fileName.startsWith("Unconfirmed") || fileName.endsWith(".tmp")) {
					// Bijan Requested 06/04
					if (fileName.startsWith("Unconfirmed") || fileName.endsWith(".tmp")
							|| fileName.endsWith(".crdownload")) {
						return false;
					} else {
						return true;
					}
				}
			});
			return DOWNLOADFILES;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * This method verify if new file is downloaded or not and also return new
	 * downloaded file name
	 *
	 * @name verifyAndGetNewDownloadedFile
	 * @description This method verify if new file is downloaded or not and also
	 *              return new downloaded file name
	 * @author Bijan Mallick
	 * @param ||description: ||allowedRange:
	 * @return String ||description: new file name
	 * @jiraId
	 */
	public static File verifyAndGetNewDownloadedFile(String[] DOWNLOADFILES) {
		try {
			int timeOut = 0;
			File downLoadDirectory = new File(BrowserManager.getInstance().getDownloadfolder());// Bijan on
																								// 19th may
																								// 2021
			String[] newDownloadFiles;
			do {
				newDownloadFiles = downLoadDirectory.list(new FilenameFilter() {
					public boolean accept(File dir, String fileName) {
						if (fileName.startsWith("Unconfirmed") || fileName.endsWith(".tmp")
								|| fileName.endsWith(".crdownload")) {
							return false;
						} else {
							return true;
						}
					}
				});
				timeOut = timeOut + 1;
				BasePage.waitForMSeconds(1000);
			} while (newDownloadFiles.length <= DOWNLOADFILES.length && timeOut < DEFAULT_TIMEOUT);
			if (newDownloadFiles.length <= DOWNLOADFILES.length) {
				// throw new FailTraceException("No New Files Downloaded");
				ExtentReportMGR.getInstance().getExtentTest().fail("No New Files Downloaded");
			}
			for (String eachNewFile : newDownloadFiles) {
				boolean status = false;
				for (String eachOldFile : DOWNLOADFILES) {
					if (eachNewFile.equalsIgnoreCase(eachOldFile)) {
						status = true;
					}
				}
				if (!status) {
					File fileObject = new File(
							BrowserManager.getInstance().getDownloadfolder() + "\\" + eachNewFile.toString());// Bijan
																												// On
																												// 19th
																												// May
																												// 2021
					ExtentReportMGR.getInstance().getExtentTest().pass(
							"File Downloaded successfully, Downloaded File Name [" + eachNewFile.toString() + "]");
					return fileObject;
				}
			}
			// throw new FailTraceException("No New Files Downloaded");
			ExtentReportMGR.getInstance().getExtentTest().fail("No New Files Downloaded");
		} catch (Exception e) {
			throw e;
		}

		// Ensure method always returns a File, even if no new file is found
		return null;
	}

	/**
	 * This method will validate ZIP content of the downloaded file
	 *
	 * @name validateZipContent()
	 * @description This method will validate ZIP content of the downloaded file
	 * @author Nilesh Patil
	 * @param String||description:zipFilePath - Path of zip file || allowedRange:
	 * @return int ||description: return 0 - if Zip has any content , return 1 - if
	 *         Zip is empty , return -1 - if any error.
	 * @throws @jiraId
	 */

	@SuppressWarnings("resource")
	public static int validateZipContent(String zipFilePath) {
		String name = null;
		long size = 0;
		long compressedSize = 0;

		try {
			File file = new File(zipFilePath);

			int flag = 0;
			ZipFile zipFile = new ZipFile(file);
			Enumeration<?> enu = zipFile.entries();
			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();
				name = zipEntry.getName();
				ExtentReportMGR.getInstance().getExtentTest().info("Content is present under zip file");
				size = zipEntry.getSize();
				compressedSize = zipEntry.getCompressedSize();
				flag = 1;
				break;
			}
			if (flag == 1) {
				ExtentReportMGR.getInstance().getExtentTest().info(" FileName is " + name + " with normal size " + size
						+ " and compressed size " + compressedSize);
				return 0;

			} else {
				ExtentReportMGR.getInstance().getExtentTest().info("ZIP file is empty");
				return 1;

			}

		} catch (IOException e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
			return -1;

		}
	}

	/*---------------------------------------------------------DATABASE OPERATION--------------------------------------------------*/

	/**
	 * create oracle database connection
	 *
	 * @name CreateDBConnection
	 * @description This method will create the connection with oracle Database
	 * @author Vaibhav Narkhede
	 * @param NA ||description: ||allowedRange:
	 * @return statement object
	 * @jiraId
	 */
	public static Statement CreateDBConnection() throws Exception {
		return DatabaseConnection.getInstance().getConnection().createStatement();
	}

	/*----------------------------------- DATE AND TIME -------------------------------------------------*/

	/**
	 * This will get current Date and time based on format requested
	 *
	 * @name getCurrentDate
	 * @description This will get current Date and time based on format requested
	 * @author Vaibhav Narkhede
	 * @param format ||description: this is a format eg. 'yyyy-MM-dd HH:mm:ss'
	 *               ||allowedRange: refer simpleDate format document to create
	 *               Valid date format
	 * @return Current Date
	 * @jiraId
	 */

	public static String getCurrentDate(String format) {
		String currentDateNTime = null;

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			currentDateNTime = sdf.format(new Date());

		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
		return currentDateNTime;

	}

	/**
	 * This method will validate ZIP content of the downloaded file
	 *
	 * @name validateZipContent()
	 * @description This method will validate ZIP content of the downloaded file
	 * @author Nilesh Patil
	 * @param File||description:file - Downloaded File Object || allowedRange:
	 * @return int ||description: return 0 - if Zip has any content , return 1 - if
	 *         Zip is empty , return -1 - if any error.
	 * @throws @jiraId
	 */

	@SuppressWarnings("resource")
	public static int validateZipContent(File file) {
		String name = null;
		long size = 0;
		long compressedSize = 0;

		try {
			int flag = 0;
			ZipFile zipFile = new ZipFile(file);
			Enumeration<?> enu = zipFile.entries();
			while (enu.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();
				name = zipEntry.getName();
				ExtentReportMGR.getInstance().getExtentTest().info("Content is present under zip file");
				size = zipEntry.getSize();
				compressedSize = zipEntry.getCompressedSize();
				flag = 1;
				break;
			}
			if (flag == 1) {
				ExtentReportMGR.getInstance().getExtentTest().info(" FileName is " + name + " with normal size " + size
						+ " and compressed size " + compressedSize);
				return 0;

			} else {
				ExtentReportMGR.getInstance().getExtentTest().info("ZIP file is empty");
				return 1;

			}

		} catch (IOException e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
			return -1;

		}
	}

	/**
	 * CreateSampleFile
	 *
	 * @name CreateSampleFile
	 * @description Create a Sample File in Desired Location
	 * @author Bijan Mallick
	 * @param ||description: ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */

	public static void CreateSampleFile(String strFilePath, String strTextValue) {

		try {
			String[] arrFilePath = strFilePath.split("\\\\");
			String strTempPath = arrFilePath[0];
			for (int i = 1; i < arrFilePath.length - 1; i++) {
				strTempPath = strTempPath + "\\" + arrFilePath[i];
				File file = new File(strTempPath);
				if (!file.exists()) {
					file.mkdir();
				}
			}
			File file = new File(strFilePath);
			if (!file.exists()) {
				file.createNewFile();
				FileUtils.writeStringToFile(file, strTextValue, "UTF-8");
				ExtentReportMGR.getInstance().getExtentTest().info(
						"[" + arrFilePath[arrFilePath.length - 1] + "] File Created in Path [" + strTempPath + "]");
			}
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
	}

	/**
	 * getSpecificDate
	 *
	 * @name getSpecificDate
	 * @description Get Date in Desired Format with Day manipulation
	 * @author Bijan Mallick
	 * @param ||description: ||allowedRange:
	 * @return String ||description:
	 * @jiraId
	 */
	public static String getSpecificDate(String Format, int AdditionalDays) {
		String NewDate = null;

		try {
			DateFormat dateFormat = new SimpleDateFormat(Format);
			Date currentDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			if (AdditionalDays != 0) {
				cal.add(Calendar.DATE, AdditionalDays);
			}
			NewDate = dateFormat.format(cal.getTime());

		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
		return NewDate;

	}

	/*
	 * RandomNumber
	 * 
	 * @name RandomNumber
	 * 
	 * @description Generate Random Number based on input length
	 * 
	 * @author Bijan Mallick
	 * 
	 * @param ||description:length ||allowedRange:
	 * 
	 * @return String ||description: Random Number String
	 * 
	 * @jiraId
	 */
	public static String RandomNumber(int length) {
		Random rand = new Random();
		String result = "";
		for (int i = 0; i < length; i++) {

			result = result + String.valueOf(rand.nextInt(8) + 1);
		}
		return result;
	}

	/*
	 * RandomString
	 * 
	 * @name RandomString
	 * 
	 * @description Generate Random String based on input length
	 * 
	 * @author Bijan Mallick
	 * 
	 * @param ||description:length ||allowedRange:
	 * 
	 * @return String ||description:Random String
	 * 
	 * @jiraId
	 */
	public static String RandomString(int length) {
		String strChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random rand = new Random();
		StringBuilder randString = new StringBuilder();
		for (int i = 0; i < length; i++) {
			randString.append(strChar.charAt(rand.nextInt(strChar.length())));
		}
		return randString.toString();
	}

	/**
	 * This will read the PDF content and return in String format portal
	 *
	 * @name readOnlinePDF
	 * @description This will read the PDF content and return in String format
	 * @author Vaibhav Narkhede
	 * @param ||description: || allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */

	public static String readBrowserPDF(String url) {
		String pdfContent = "";
		try {
			URL url1 = new URL(url);
			InputStream is = url1.openStream();
			BufferedInputStream parsefile = new BufferedInputStream(is);
			PDDocument document = null;
			document = PDDocument.load(parsefile);
			pdfContent = new PDFTextStripper().getText(document);
			return pdfContent;
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
		return pdfContent;
	}

	/**
	 * This will read the PDF content from local machine
	 *
	 * @name readPDF
	 * @description This will read the PDF content and return in String format
	 * @author Vaibhav Narkhede
	 * @param String ||description: local pdf Path || allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */

	public static String readSystemPDF(String pdfPath) {
		String pdfContent = "";
		try {
			File parsefile = new File(pdfPath);
			PDDocument document = null;
			document = PDDocument.load(parsefile);
			pdfContent = new PDFTextStripper().getText(document);
			document.close();
			parsefile = null;
			return pdfContent;
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(e);
		}
		return pdfContent;
	}

	/**
	 * getAllFilesInSpecificDirectory
	 *
	 * @name getAllFilesInSpecificDirectory
	 * @description Fetch all files Present in Specific Folder
	 * @author Bijan Mallick
	 * @param ||description: ||allowedRange:
	 * @return String[] ||description:All Files
	 * @jiraId
	 */
	public static String getAllFilesInSpecificDirectory(String FileBasePath) throws Exception {
		try {
			File downLoadDirectory = new File(FileBasePath.toString());
			File[] files = downLoadDirectory.listFiles();
			String filesList = "";
			for (int i = 0; i < files.length; i++) {
				filesList += (i != 0 ? "\n" : "") + files[i].getAbsolutePath();
			}
			return filesList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * This method fetches data from a database based on the provided SQL query. It
	 * returns a map where the key is the row number and the value is another map.
	 * The inner map's key is the column name and the value is the corresponding
	 * data from the database.
	 *
	 * @param strQuery The SQL query to be executed.
	 * @return A map containing the data fetched from the database.
	 * @throws Exception If an error occurs during execution.
	 * @author BMallick
	 */
	public static Map<Integer, Map<String, String>> FetchDataFromDBGeneric(String strQuery) {
		Map<Integer, Map<String, String>> returnMapList = new HashMap<Integer, Map<String, String>>();
		Map<String, String> returnMap = new HashMap<>();
		try {
			Statement stmt = BasePage.CreateDBConnection();
			ResultSet rs = stmt.executeQuery(strQuery);
			ExtentReportMGR.getInstance().getExtentTest().info("Executing DB Query[" + strQuery + "]");
			String[] attr = strQuery.split(" ")[1].toUpperCase().split(",");
			int recordCount = -1;
			while (rs.next()) {
				for (String eachAttr : attr) {
					if (rs.getString(eachAttr) == null)
						returnMap.put("", rs.getString(eachAttr));
					else
						returnMap.put(eachAttr, rs.getString(eachAttr));
				}
				returnMapList.put(++recordCount, returnMap);
			}
			stmt.close();
			ExtentReportMGR.getInstance().getExtentTest()
					.info(returnMapList.size() + " Records Found, Closing DB Connection ...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMapList;
	}

	public <K, V> Map<K, V> createGenericMap(K key, V value) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(key, value);
		return map;
	}

	/**
	 * Generates a Time-based One-Time Password (TOTP) using the provided secret
	 * key.
	 *
	 * @param secretKey       The secret key used to generate the TOTP.
	 * @param timeStepSeconds The time step in seconds for the TOTP generation.
	 * @return A 6-digit TOTP as a String.
	 */
	public String generateTOTP(String secretKey, int timeStepSeconds) {
		// Create an instance of GoogleAuthenticator
		GoogleAuthenticator gAuth = new GoogleAuthenticator();

		// Calculate the time left for the next TOTP generation
		int timeLeft = (int) (timeStepSeconds - ((System.currentTimeMillis() / 1000) % timeStepSeconds));
		System.out.println("Time left for next TOTP: " + timeLeft + " seconds");

		// If the time left is less than or equal to the time step minus 28 seconds,(2
		// seconds left)
		// wait for the next TOTP generation
		if (timeLeft <= timeStepSeconds - 28) {
			try {
				System.out.println("Waiting for " + timeLeft + " seconds to generate a new TOTP...");
				Thread.sleep((timeLeft + 2) * 1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException("Thread was interrupted", e);
			}
		}

		// Generate the TOTP and format it as a 6-digit zero-padded string
		String totp = String.format("%06d", gAuth.getTotpPassword(secretKey));
		return totp;
	}

	/**
	 * Fills a web element using Playwright. Waits until the element is visible and
	 * enabled, then fills it with the provided text. Handles errors and logs
	 * failures.
	 *
	 * @param locator The Playwright Locator for the element to fill.
	 * @param text    The text to fill into the element.
	 * @author BMallick
	 */
	public void fill(Object element, String text, String... optionalElementName) {
		String printName = (optionalElementName.length > 0) ? optionalElementName[0] : null;
		try {
			Locator locator;
			if (element instanceof Locator) {
				locator = (Locator) element;
			} else if (element instanceof String) {
				locator = BrowserManager.getInstance().getPage().locator(element.toString());
			} else {
				throw new IllegalArgumentException("Parameter must be either Locator or String");
			}

			locator.waitFor(
					new Locator.WaitForOptions().setTimeout(DEFAULT_TIMEOUT).setState(WaitForSelectorState.VISIBLE));
			if (!locator.isEnabled()) {
				Exception e = new Exception("Element is not enabled.");
				exception(e);
				return;
			}
			locator.fill(text);
			if (printName != null)
				info("Filled " + printName + " with text: " + text);
		} catch (Exception e) {
			if (printName != null)
				fail("Failed to fill " + printName + " with text: " + text);
			else
				exception(e, false);
			throw new RuntimeException(
					"Failed to fill element" + (printName != null ? " " + printName : "") + ": " + e.getMessage(), e);
		}
	}

	/**
	 * Opens the specified URL in the browser using Playwright. Logs the action and
	 * handles errors.
	 *
	 * @param url The URL to open.
	 * @author BMallick
	 */
	public static void openURL(String url) {
		try {
			Page page = BrowserManager.getInstance().getPage();
			page.navigate(url);
			ExtentReportMGR.getInstance().getExtentTest()
					.info("Navigated to URL: <a href='" + url + "' target='_blank'>" + url + "</a>");
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error(
					"Failed to open URL: <a href='" + url + "' target='_blank'>" + url + "</a>" + e.getMessage());
			throw new RuntimeException("Failed to open URL '" + url + "': " + e.getMessage(), e);
		}
	}

	/**
	 * Checks if an element is present and visible on the page using Playwright.
	 *
	 * @param element The Playwright Locator or selector string for the element.
	 * @return true if the element is present and visible, false otherwise.
	 * @author BMallick
	 */
	public static boolean isElementPresent(Object element) {
		try {
			Locator locator;
			if (element instanceof Locator) {
				locator = (Locator) element;
			} else if (element instanceof String) {
				locator = BrowserManager.getInstance().getPage().locator(element.toString());
			} else {
				throw new IllegalArgumentException("Parameter must be either Locator or String");
			}
			return locator.count() > 0 && locator.isVisible();
		} catch (Exception e) {
			ExtentReportMGR.getInstance().getExtentTest().error("Error checking element presence: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Generates a timestamp string representing the current date and time in the
	 * format "yyyyMMdd_HHmmss".
	 *
	 * @return A string representing the current date and time.
	 */
	public static String generateCurrentDateandTimeStamp() {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
			String timestamp = formatter.format(new Date());

			return timestamp;

		} catch (Exception e) {
			return "InvalidTimestamp";
		}
	}

	/**
	 * Retrieves the value of a property from the configuration file.
	 *
	 * @param envValue The key of the property to retrieve.
	 * @return The value of the property, or null if an error occurs.
	 */
	public String getProperty(String envValue) {
		try {
			return BaseTest.prop.getProperty(envValue);

		} catch (Exception e) {
			exception(e);
		}
		return null;

	}

	/**
	 * Waits for an element to become visible on the page within a specified
	 * timeout.
	 *
	 * @param page             The Playwright Page object.
	 * @param selector         The selector of the element to wait for.
	 * @param timeoutInSeconds The maximum time to wait in seconds.
	 * @return true if the element becomes visible within the timeout, false
	 *         otherwise.
	 * @author BMallick
	 */
	public static boolean waitForElementVisible(Page page, String selector, int timeoutInSeconds) {
		try {
			Locator locator = page.locator(selector);
			locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)
					.setTimeout(timeoutInSeconds * 1000)); // timeout in milliseconds
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Checks if an element is present and visible on the page using Playwright.
	 *
	 * @param page     The Playwright Page object.
	 * @param selector The Playwright selector for the element.
	 * @return true if the element is present and visible, false otherwise.
	 * @author BMallick
	 */
	public static boolean isElementPresent(Page page, String selector) {
		try {
			// Optional wait
			page.waitForTimeout(1000);

			Locator locator = page.locator(selector);
			int count = locator.count();

			if (count == 0) {
				return false;
			}

			return locator.first().isVisible();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Performs a double-click action on the specified element using Playwright.
	 *
	 * @param element The Playwright Locator or selector string of the element to
	 *                double-click.
	 * @author BMallick
	 */
	public static void dblClick(Object element) {
		try {
			Locator locator;
			if (element instanceof Locator) {
				locator = (Locator) element;
			} else if (element instanceof String) {
				locator = BrowserManager.getInstance().getPage().locator(element.toString());
			} else {
				throw new IllegalArgumentException("Parameter must be either Locator or String");
			}

			waitForVisible(locator);

			if (!locator.isEnabled()) {
				throw new RuntimeException("Locator is not enabled.");
			}

			locator.dblclick(new Locator.DblclickOptions().setTimeout(timeout.toMillis()));
		} catch (Exception e) {
			throw new RuntimeException("Failed to double-click element: " + element, e);
		}
	}

	// ----- Wait Methods -----
	/**
	 * Waits for a locator to be present on the page within a specified timeout.
	 *
	 * @param locator The Playwright Locator to wait for.
	 * @param timeout The maximum time to wait as a Duration.
	 * @return true if the locator is found within the timeout, false otherwise.
	 * @author BMallick
	 */
	public static boolean waitForLocator(Object element, int timeoutMs) {
		try {
			Locator locator;
			if (element instanceof Locator) {
				locator = (Locator) element;
			} else if (element instanceof String) {
				locator = BrowserManager.getInstance().getPage().locator(element.toString());
			} else {
				throw new IllegalArgumentException("Parameter must be either Locator or String");
			}
			locator.waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Waits for a locator to be present on the page using the default timeout.
	 *
	 * @param locator The Playwright Locator to wait for.
	 * @return true if the locator is found within the default timeout, false
	 *         otherwise.
	 * @author BMallick
	 */
	public static boolean waitForLocator(Object element) {
		return waitForLocator(element, DEFAULT_TIMEOUT);
	}

	public static boolean waitForLocators(List<Locator> locators, int timeoutMs) {
		long end = System.currentTimeMillis() + timeoutMs;
		long pollInterval = 200; // ms

		while (System.currentTimeMillis() < end) {
			for (Locator locator : locators) {
				try {
					if (locator.isVisible()) {
						return true;
					}
				} catch (Exception ignored) {
					// Ignore and continue
				}
			}
			try {
				Thread.sleep(pollInterval);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		return false;
	}

	/**
	 * Waits for a locator to become visible on the page within a specified timeout.
	 *
	 * @param locator The Playwright Locator to wait for.
	 * @param timeout The maximum time to wait as a Duration.
	 * @author BMallick
	 */
	public static boolean waitForVisible(Object element, int timeoutMs) {
		try {
			Locator locator;
			if (element instanceof Locator) {
				locator = (Locator) element;
			} else if (element instanceof String) {
				locator = BrowserManager.getInstance().getPage().locator(element.toString());
			} else {
				return false;
			}
			locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeoutMs));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Waits for a locator to become visible on the page using the default timeout.
	 *
	 * @param locator The Playwright Locator to wait for.
	 * @author BMallick
	 */
	public static boolean waitForVisible(Object element) {
		return waitForVisible(element, DEFAULT_TIMEOUT);
	}

	/**
	 * Waits for a locator to become invisible on the page within a specified
	 * timeout.
	 *
	 * @param locator The Playwright Locator to wait for.
	 * @param timeout The maximum time to wait as a Duration.
	 * @return true if the locator becomes invisible within the timeout, false
	 *         otherwise.
	 * @author BMallick
	 */
	public static boolean waitForInvisible(Object element, int... timeout) {
		int tempTimeout = (timeout.length > 0) ? timeout[0] : DEFAULT_TIMEOUT;
		try {
			Locator locator;
			if (element instanceof Locator) {
				locator = (Locator) element;
			} else if (element instanceof String) {
				locator = BrowserManager.getInstance().getPage().locator(element.toString());
			} else {
				throw new IllegalArgumentException("Parameter must be either Locator or String");
			}
			locator.waitFor(
					new Locator.WaitForOptions().setState(WaitForSelectorState.DETACHED).setTimeout(tempTimeout));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Waits for a locator to become hidden on the page within a specified timeout.
	 *
	 * @param locator The Playwright Locator to wait for.
	 * @param timeout The maximum time to wait as a Duration.
	 * @author BMallick
	 */
	public void waitForHidden(Object element, int timeoutMs) {
		try {
			Locator locator;
			if (element instanceof Locator) {
				locator = (Locator) element;
			} else if (element instanceof String) {
				locator = BrowserManager.getInstance().getPage().locator(element.toString());
			} else {
				throw new IllegalArgumentException("Parameter must be either Locator or String");
			}
			locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(timeoutMs));
		} catch (Exception e) {
			warning("Exception in waitForHidden: " + e.getMessage());
		}
	}

	/**
	 * Waits for a locator to become hidden on the page using the default timeout.
	 *
	 * @param locator The Playwright Locator to wait for.
	 * @author BMallick
	 */
	public void waitForHidden(Object element) {
		try {
			waitForHidden(element, DEFAULT_TIMEOUT);
		} catch (Exception e) {
			warning("Exception in waitForHidden: " + e.getMessage());
		}
	}

	// ----- Screenshots / Tracing -----
	/**
	 * Takes a screenshot of the entire page.
	 *
	 * @param page The Playwright Page object.
	 * @return A byte array containing the screenshot image data.
	 * @author BMallick
	 */
	public byte[] screenshotWholePage(Page page) {
		return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
	}

	/**
	 * Takes a screenshot of a specific element on the page.
	 *
	 * @param page     The Playwright Page object.
	 * @param selector The selector of the element to screenshot.
	 * @return A byte array containing the screenshot image data of the element.
	 * @author BMallick
	 */
	public byte[] screenshotOfElement(Page page, String selector) {
		Locator loc = page.locator(selector);
		return loc.screenshot();
	}

	// ----- Retry wrapper for flaky actions -----
	/**
	 * Retry an action a specified number of times with a polling interval.
	 * 
	 * @param action
	 * @param attempts
	 * @param pollInterval
	 * @author BMallick
	 */
	public void retry(Runnable action, int attempts, Duration pollInterval) {
		PlaywrightException last = null;
		for (int i = 0; i < attempts; i++) {
			try {
				action.run();
				return;
			} catch (PlaywrightException e) {
				last = e;
				try {
					Thread.sleep(pollInterval.toMillis());
				} catch (InterruptedException ignored) {
				}
			}
		}
		if (last != null)
			throw last;
	}

	/**
	 * Retry an action using default settings.
	 * 
	 * @param action
	 * @author BMallick
	 */
	public void retry(Runnable action) {
		retry(action, defaultRetryAttempts, defaultPollInterval);
	}

	/**
	 * Retry an action until a condition is met or the maximum number of attempts is
	 * reached.
	 * 
	 * @param action
	 * @param condition
	 * @param attempts
	 * @param pollInterval
	 * @author BMallick
	 */
	public void retryUntil(Runnable action, Supplier<Boolean> condition, int attempts, Duration pollInterval) {
		for (int i = 0; i < attempts; i++) {
			action.run(); // Try clicking the button
			if (condition.get()) {
				return; // Success: window is closed
			}
			try {
				Thread.sleep(pollInterval.toMillis());
			} catch (InterruptedException ignored) {
			}
		}
		throw new RuntimeException("Condition not met after " + attempts + " attempts");
	}

	// -----------------------
	/**
	 * Mouse hover to element.
	 *
	 * @name hoverOverToElement
	 * @description Mouse hover to element
	 * @author Bijan Mallick
	 * @param by ||description: Element's Locator ||allowedRange:
	 * @return void ||description:
	 */
	public static void hover(Locator locator) {
		try {
			locator.hover();
		} catch (Exception e) {
		}
	}

	/**
	 * waitForPageLoad
	 *
	 * @name waitForPageLoad
	 * @description Wait for page load to complete
	 * @author BMallick
	 * @param ||description: ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */
	public void waitForPageLoad() {
		waitForPageLoad(LONG_TIMEOUT);
	}

	/**
	 * waitForPageLoad
	 *
	 * @name waitForPageLoad
	 * @description Wait for page load to complete
	 * @author BMallick
	 * @param timeoutMillis ||description: timeout in milliseconds ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */
	public void waitForPageLoad(long timeoutMillis) {
		try {
			info("Waiting for page to load completely");
			Page page = BrowserManager.getInstance().getPage();
			// Wait for the initial load event
			page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(timeoutMillis));
			// Try waiting for network idle, but don't block if it takes too long
			try {
				page.waitForLoadState(LoadState.NETWORKIDLE,
						new Page.WaitForLoadStateOptions().setTimeout(QUICK_TIMEOUT));
			} catch (Exception ignored) {
			}
			page.waitForTimeout(500);
			info("Page loaded successfully");
		} catch (Exception e) {
			error("Page load timeout: " + e.getMessage());
			throw new RuntimeException("Page load timeout: " + e.getMessage(), e);
		}
	}

	/**
	 * generateNumber
	 *
	 * @name generateNumber
	 * @description Generate Random Number between min and max
	 * @author Vaibhav Narkhede
	 * @param min ||description: minimum number ||allowedRange:
	 * @param max ||description: maximum number ||allowedRange:
	 * @return int ||description: Random Number
	 * @jiraId
	 */
	public int generateNumber(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("Min should be less than or equal to Max");
		}

		Random random = new Random();
		return random.nextInt((max - min) + 1) + min;
	}

	/**
	 * waitForEnable
	 *
	 * @name waitForEnable
	 * @description Wait until the element is enabled
	 * @author Vaibhav Narkhede
	 * @param locator ||description: Element's Locator ||allowedRange:
	 * @return void ||description:
	 * @jiraId
	 */
	public void waitForEnable(Locator locator) {
		try {

			// Wait until the element is visible first
			locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

			// Poll until it's enabled (with timeout)
			int timeoutMs = MEDIUM_TIMEOUT; // 10 Sec
			int intervalMs = 250;
			int waited = 0;

			while (!locator.isEnabled() && waited < timeoutMs) {
				BrowserManager.getInstance().getPage().waitForTimeout(intervalMs);
				waited += intervalMs;
			}

		} catch (Exception e) {
			exception(e);
		}
	}

	public Function<String, Locator> tableRowWithCellTextLocator = cellText -> BrowserManager.getInstance().getPage()
			.locator("tr:has(td:has-text('" + cellText + "'))");

	public Function<String, Locator> buttonWithTextLocator = buttonText -> BrowserManager.getInstance().getPage()
			.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonText));

	public Function<String, Locator> exactTextLocator = text -> BrowserManager.getInstance().getPage().getByText(text,
			new Page.GetByTextOptions().setExact(true));

	public Function<String, Locator> textboxWithTextLocator = name -> BrowserManager.getInstance().getPage()
			.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(name));
	public Function<String, Locator> imageWithTextLocator = name -> BrowserManager.getInstance().getPage()
			.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(name));
	public Function<String, Locator> getLocator = locator -> BrowserManager.getInstance().getPage().locator(locator);

	/**
	 * Performs SSO login for the given URL. Handles user authentication,
	 * verification code, and post-login steps.
	 * 
	 * @param strURL The URL to open and login
	 * @return true if login is successful, false otherwise
	 */
	public boolean ssoLogin() {
		final String secretKey = Util.decryptString(System.getenv("SSO_TOTP_SECRET_ENC"));
		final int timeStepSeconds = 30;
		boolean loginStatus = false;
		String loginUserID = System.getProperty("user.name") + "@eagle.org";

		try {
			List<Locator> locators = Arrays.asList(imageWithTextLocator.apply("Microsoft"), // SSO Login Page
					textboxWithTextLocator.apply("User Name"), // Freedom Login Page
					textboxWithTextLocator.apply("User Name:") // Maximo Login Page
			// getLocator.apply("input#a"), // Freedom Login Page
			// getLocator.apply("#j_username")// Maximo Login Page
			// Windchill Login Page
			);

			waitForLocators(locators, DEFAULT_TIMEOUT);
			if (!imageWithTextLocator.apply("Microsoft").isVisible()) {
				info("SSO Login not required for this environment");
				return false;
			}

			// waitForLocator(imageWithTextLocator.apply("Eagle"),
			// Duration.ofSeconds(DEFAULT_TIMEOUT));

			// Prepare all required locators from the page object
			String authentication = "(//*[@id='tilesHolder']//*[text()='" + loginUserID
					+ "'])|(//input[@type='email'])|(//input[@id='a'])";
			String emailId = "input[type='email']";
			String ssoPassword = "input[type='password']";
			String verificationCodeOption = "//*[contains(text(),'Use a verification code')]";
			String microsoftAuthenticatorApp = "//a[contains(text(),'Microsoft Authenticator app')]";
			String totpInput = "#idTxtBx_SAOTCC_OTC";
			String errorMsgOTC = "//*[contains(@id,'SAOTCC_ErrorMsg_OTC')]";

			String yesButton = "//input[@value='Yes']";
			String windchillUseraName = "identifierInput";

			if (waitForElementVisible(emailId, 10) || waitForElementVisible(ssoPassword, 10)) {
				handleAuthentication(emailId, ssoPassword, loginUserID);
				handleVerification(verificationCodeOption, microsoftAuthenticatorApp, totpInput, errorMsgOTC, secretKey,
						timeStepSeconds);
				// Handle post-login confirmation
				handlePostLogin(yesButton);
				waitForPageLoad();
				pass("SSO Login successful for user: " + loginUserID, true);
				return loginStatus = true;
			}

			// BrowserManager.getInstance().getPage().locator("[data-test-id='bmallick@eagle.org']").isVisible();
			// Special handling for Windchill login page
			/*
			 * waitForPageLoad(); refreshPage(); String strURL =
			 * BrowserManager.getInstance().getPage().url();
			 * 
			 * if (strURL.contains("SSO")) { fill(windchillUseraName, loginUserID);
			 * BrowserManager.getInstance().getPage().keyboard().press("Enter");
			 * 
			 * } else if (!strURL.contains("microsoftonline")) {
			 * info("SSO login not implemented for this environment: " + strURL); return
			 * false; // SSO login not implemented for this environment
			 * 
			 * }
			 */

		} catch (Exception e) {
			error("An error occurred during SSO login: " + e.getMessage());
		}
		fail("SSO Login failed for user: " + loginUserID, true);
		return loginStatus;
	}

	/**
	 * Handles user authentication by entering email and password if required.
	 * 
	 * @param emailId     Locator for email input
	 * @param ssoPassword Locator for password input
	 * @param loginUserID The user email to enter
	 */
	private void handleAuthentication(String emailId, String ssoPassword, String loginUserID) {
		if (BasePage.isElementPresent(emailId)) {
			fill(emailId, loginUserID);
			BrowserManager.getInstance().getPage().keyboard().press("Enter");
			waitForMSeconds(1000);
			if (BasePage.waitForElementVisible(ssoPassword, 10)) {
				clear(ssoPassword);
				BrowserManager.getInstance().getPage().locator(ssoPassword)
						.fill(Util.decryptString(System.getenv("SSO_PASSWORD_ENC")));
				info("SSO Password entered");
				// fill(ssoPassword, Util.decryptString(System.getenv("SSO_PASSWORD_ENC")));
				BrowserManager.getInstance().getPage().keyboard().press("Enter");
			}
		} else if (BasePage.isElementPresent("//*[text()='" + loginUserID.toLowerCase() + "']")) {

			if (BasePage.waitForElementVisible(ssoPassword, 10)) {
				clear(ssoPassword);
				BrowserManager.getInstance().getPage().locator(ssoPassword)
						.fill(Util.decryptString(System.getenv("SSO_PASSWORD_ENC")));
				info("SSO Password entered");
				BrowserManager.getInstance().getPage().keyboard().press("Enter");
			}

		} else {
			click("//input[@id='a']");
		}
	}

	/**
	 * Handles verification code entry using Microsoft Authenticator app and TOTP.
	 * 
	 * @param verificationCodeOption    Locator for verification code option
	 * @param microsoftAuthenticatorApp Locator for authenticator app link
	 * @param totpInput                 Locator for TOTP input
	 * @param errorMsgOTC               Locator for error message
	 * @param secretKey                 Secret key for TOTP generation
	 * @param timeStepSeconds           Time step for TOTP
	 */
	private void handleVerification(String verificationCodeOption, String microsoftAuthenticatorApp, String totpInput,
			String errorMsgOTC, String secretKey, int timeStepSeconds) {
		// Create a list of possible verification locators
		List<Locator> locators = Arrays.asList(getLocator.apply(verificationCodeOption),
				getLocator.apply(microsoftAuthenticatorApp));
		// Wait until any of the locators is visible
		waitForLocators(locators, QUICK_TIMEOUT);

		if (isElementPresent(microsoftAuthenticatorApp)) {
			click(microsoftAuthenticatorApp);
		}
		click(verificationCodeOption);

		// Generate and enter TOTP
		String totp = generateTOTP(secretKey, timeStepSeconds);
		enterTOTP(totpInput, errorMsgOTC, totp, secretKey, timeStepSeconds);
	}

	/**
	 * Enters the TOTP and handles error if the code is incorrect.
	 * 
	 * @param totpInput       Locator for TOTP input
	 * @param errorMsgOTC     Locator for error message
	 * @param totp            The TOTP code
	 * @param secretKey       Secret key for TOTP
	 * @param timeStepSeconds Time step for TOTP
	 */
	private void enterTOTP(String totpInput, String errorMsgOTC, String totp, String secretKey, int timeStepSeconds) {
		fill(totpInput, totp);
		BrowserManager.getInstance().getPage().keyboard().press("Enter");
		// info("Verification code entered: " + totp, true);

		if (isElementPresent(errorMsgOTC)) {
			warning("Verification code is incorrect");
			totp = generateTOTP(secretKey, timeStepSeconds);
			info("Regenerated TOTP: " + totp);
			clear(totpInput);
			fill(totpInput, totp);
			BrowserManager.getInstance().getPage().keyboard().press("Enter");
			// info("Verification code entered: " + totp, true);
		}
	}

	/**
	 * Handles post-login confirmation (e.g., clicking 'Yes' button).
	 * 
	 * @param yesButton Locator for Yes button
	 */
	private void handlePostLogin(String yesButton) {
		click(yesButton);
	}

	/**
	 * Constructs a Jira issue link based on the provided Jira key.
	 *
	 * @param jiraKey The Jira issue key (e.g., "PROJECT-123").
	 * @return A formatted HTML link to the Jira issue.
	 * @author BMallick
	 */
	protected String getJiraIssueLink(String jiraKey) {
		return String.format(
				" [<a href='https://americanbureauofshipping.atlassian.net/browse/%s' target='_blank'>%s</a>]", jiraKey,
				jiraKey);
	}

	/**
	 * Clicks on an element specified by locator or text and waits for a new page to
	 * open.
	 *
	 * @param locatorOrText The locator (Locator) or exact text (String) of the
	 *                      element to click.
	 * @return The newly opened Page object.
	 * @author BMallick
	 */
	public Page clickAndWaitForNewPage(Object locatorOrText) {
		try {
			BrowserContext context = BrowserManager.getInstance().getContext();
			Page newPage = context.waitForPage(() -> {
				BrowserManager.getInstance().getPage().waitForPopup(() -> {
					if (locatorOrText instanceof String) {
						click(exactTextLocator.apply((String) locatorOrText));
					} else {
						click(((Locator) locatorOrText));
					}
					BasePage.waitForMSeconds(QUICK_TIMEOUT);
				});
			});
			BrowserManager.setPage(newPage);
			return newPage;
		} catch (Exception e) {
			exception(e);
			fail("No new page opened after clicking the element or Locator is not present to click.");
			BrowserManager.setPage(BrowserManager.getInstance().getPage());
			return BrowserManager.getInstance().getPage();
		}
	}
	/**
	 * Waits for a page to be closed within a specified timeout.
	 *
	 * @param page          The Playwright Page object to monitor.
	 * @param timeoutMillis The maximum time to wait in milliseconds.
	 * @return true if the page is closed within the timeout, false otherwise.
	 * @author BMallick
	 */
	public boolean waitForPageInexistence(Page page, long timeoutMillis) {
		try {
			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < timeoutMillis) {
				if (page.isClosed()) {
					return true;
				}
				try {
					Thread.sleep(200); // Polling interval
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
			return page.isClosed();
		} catch (Exception e) {
			return false;
		}
	}
}
