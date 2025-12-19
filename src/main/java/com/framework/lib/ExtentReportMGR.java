
package com.framework.lib;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public final class ExtentReportMGR {
    private static final ExtentReportMGR INSTANCE = new ExtentReportMGR();
    private final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private final ThreadLocal<Integer> stepNum = ThreadLocal.withInitial(() -> 1);
    public ThreadLocal<Integer> getStepNum() {
		return stepNum;
	}

	private ExtentReports extentReports;

    private ExtentReportMGR() {}
    /**
     * This method is used to get the singleton instance of ExtentReportMGR.
     * ExtentReportMGR is a singleton class, which means it allows only one instance of itself to be created.
     * This method provides a global point of access to the ExtentReportMGR instance.
     *
     * @return ExtentReportMGR singleton instance
     * @author Satyajit
     */
    public static ExtentReportMGR getInstance() {
        return INSTANCE;
    }

	/**
	 * This method is used to get the ExtentTest instance associated with the
	 * current thread. The ExtentTest instance is stored in a ThreadLocal variable,
	 * which ensures that each thread has its own isolated instance.
	 *
	 * @return ExtentTest instance associated with the current thread
	 * @author Satyajit
	 */
	public ExtentTest getExtentTest() {
		return extentTest.get();
	}

	/**
	 * This method is used to set the ExtentTest instance associated with the
	 * current thread. The ExtentTest instance is stored in a ThreadLocal variable,
	 * which ensures that each thread has its own isolated instance.
	 *
	 * @param extent ExtentTest instance to be associated with the current thread
	 * @author Satyajit
	 */
	public void setExtentTest(ExtentTest extent) {
		extentTest.set(extent);
		getStepNum().set(1);
	}

	/**
	 * This method is used to create a new node in the ExtentTest instance
	 * associated with the current thread. It first creates a new node in the given
	 * ExtentTest instance with the provided node name. Then, it sets this new node
	 * as the ExtentTest instance for the current thread. After that, it flushes the
	 * ExtentReports instance to write the changes to the report. Finally, it
	 * returns the ExtentTest instance associated with the current thread.
	 *
	 * @param extent   ExtentTest instance in which the new node is to be created
	 * @param nodeName Name of the new node
	 * @return ExtentTest instance associated with the current thread
	 * @author Satyajit
	 */

	public ExtentTest setExtentTestNode(ExtentTest extent, String nodeName) {
		int currentStepNum = getStepNum().get();
		extentTest.set(extent.createNode("Step " + currentStepNum+":" + nodeName));
		getStepNum().set(currentStepNum + 1);
		//extentReports.flush();
		return getExtentTest();
	}

	/**
	 * This method is used to remove the ExtentTest instance associated with the
	 * current thread. It first checks if the ExtentTest instance is not null, then
	 * it removes the instance from the ThreadLocal variable. This ensures that the
	 * ExtentTest instance is properly cleaned up and prevents memory leaks.
	 *
	 * @author Satyajit
	 */
	public void removeExtentTest() {
		if (extentTest.get() != null) {
			extentTest.remove();
			extentTest.set(null);
		}
		if (stepNum.get() != null) {
			stepNum.remove();			
		}
	}
	/**
	 * This method is used to get the ExtentReports instance. ExtentReports is a
	 * class from the ExtentReports library, which is used for creating HTML reports
	 * in Selenium WebDriver. This method provides a global point of access to the
	 * ExtentReports instance.
	 *
	 * @return ExtentReports instance
	 * @author Satyajit
	 */
	public ExtentReports getExtentReports() {
		return extentReports;
	}

	/**
	 * This method is used to set the ExtentReports instance. ExtentReports is a
	 * class from the ExtentReports library, which is used for creating HTML reports
	 * in Selenium WebDriver. This method allows to set a new ExtentReports
	 * instance.
	 *
	 * @param extentReports ExtentReports instance to be set
	 * @author Satyajit
	 */
	public void setExtentReports(ExtentReports extentReports) {
		this.extentReports = extentReports;
	}
	public void removeCurrentTest(final ExtentReports reports, final ExtentTest extentTest) {
        reports.removeTest(extentTest);
}

}