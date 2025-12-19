package com.framework.logger;

import java.io.File;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.MediaEntityModelProvider;
import com.framework.interfaces.ILogger;
import com.framework.lib.ExtentReportMGR;
import com.framework.lib.Util;
import com.microsoft.playwright.PlaywrightException;


public class ListenersTestNG implements ITestListener, ISuiteListener, ILogger {
	@Override
	public void onFinish(ITestContext context) {
		System.out.println("onFinish method started");
	}

	@SuppressWarnings("static-access")
	@Override
	public void onStart(ISuite suite) {
		String suiteName = (new File(suite.getXmlSuite().getFileName())).getName().replace(".xml", "");
		Util.getInstance().XMLSUITE_NAME = suiteName;
		System.out.println("Suite Name: " + suiteName);
	}

	@Override
	public void onFinish(ISuite suite) {
		// ExtentReportMGR.getInstance().getExtentReports().flush();
		// BrowserManager.getInstance().quit();
	}

	@Override
	public void onTestStart(ITestResult result) {
		String testName = result.getMethod().getMethodName();

		System.out.println("Test Started: " + testName);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = result.getMethod().getMethodName();

		System.out.println("Test Finished: " + testName + " | Status: \u001B[32mSUCCESS\u001B[0m");

		ITestListener.super.onTestSuccess(result);
	}

	@Override
	public void onTestFailure(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		try {
			MediaEntityModelProvider screenshot = getScreenShot();
			if (screenshot != null) {
				ExtentReportMGR.getInstance().getExtentTest().fail(testName + " Failed and screenshot attached",
						screenshot);
			} else {
				ExtentReportMGR.getInstance().getExtentTest().fail(testName + " Failed but no screenshot available");
			}
		} catch (PlaywrightException e) {
			System.out.println("Playwright error capturing screenshot in onTestFailure listener");
		} catch (Exception e) {
			System.out.println("Error in onTestFailure listener");
		} finally {
			System.out.println("Test Finished: " + testName + " | Status: \u001B[31mFAILED\u001B[0m");
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = result.getMethod().getMethodName();
		try {
			MediaEntityModelProvider screenshot = getScreenShot();
			if (screenshot != null) {
				ExtentReportMGR.getInstance().getExtentTest().skip(testName + " Skipped and screenshot attached",
						screenshot);
			} else {
				ExtentReportMGR.getInstance().getExtentTest().skip(testName + " Skipped but no screenshot available");
			}
		} catch (PlaywrightException e) {
			System.out.println("Playwright error capturing screenshot in onTestSkipped listener");
		} catch (Exception e) {
			System.out.println("Error in onTestSkipped listener");
		} finally {
			System.out.println("Test Finished: " + testName + " | Status: \u001B[33mSKIPPED\u001B[0m");
		}
	}

}