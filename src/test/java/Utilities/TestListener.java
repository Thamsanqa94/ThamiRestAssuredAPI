package Utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG Listener for automatic ExtentReports integration
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        // Initialize reports once before all tests
        ExtentReportManager.initReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Create a new test for each test method
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();

        if (description != null && !description.isEmpty()) {
            ExtentReportManager.createTest(testName, description);
        } else {
            ExtentReportManager.createTest(testName);
        }

        ExtentReportManager.logInfo("Test started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.logPass("Test passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentReportManager.logFail("Test failed: " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportManager.logSkip("Test skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        // Flush reports after all tests complete
        ExtentReportManager.flushReports();
    }
}