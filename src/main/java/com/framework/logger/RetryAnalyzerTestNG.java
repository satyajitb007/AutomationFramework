package com.framework.logger;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import com.framework.lib.ExtentReportMGR;

public class RetryAnalyzerTestNG implements IRetryAnalyzer {
    private ThreadLocal<Integer> count = ThreadLocal.withInitial(() -> 0);
    private static final int MAX_RETRY_COUNT = 1;
    private static final RetryAnalyzerTestNG instance = new RetryAnalyzerTestNG();

    public static RetryAnalyzerTestNG getInstance() {
        return instance;
    }

    public Integer getCount() {
        return count.get();
    }

    public void setRetryCount(int currentCount) {
        count.set(currentCount);
    }

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) {
            if (getCount() < MAX_RETRY_COUNT) {
                setRetryCount(getCount() + 1);
                System.out.println("Retry(" + getCount() + ") for test: " + iTestResult.getMethod().getMethodName());
                ExtentReportMGR.getInstance().removeCurrentTest(
                    ExtentReportMGR.getInstance().getExtentReports(),
                    ExtentReportMGR.getInstance().getExtentTest()
                );
                return true;
            } else {
                System.out.println("Retry failed for test: " + iTestResult.getMethod().getMethodName());
            }
        } else {
            System.out.println("Retry success for test: " + iTestResult.getMethod().getMethodName());
        }
        return false;
    }
}