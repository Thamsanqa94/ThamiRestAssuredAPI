package Utilities;

import com.aventstack.extentreports.ExtentTest;
import org.testng.*;
import java.lang.reflect.Method;

/**
 * TestNG Listener for ExtentReports integration
 */
public class ExtentTestNGListener implements ITestListener, ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        // Initialize reports when suite starts
        ExtentReportManager.initReports();
    }

    @Override
    public void onFinish(ISuite suite) {
        // Flush reports when suite finishes
        ExtentReportManager.flushReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Create a new test in the report
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        String testName = result.getMethod().getMethodName();
        String description = method.getAnnotation(org.testng.annotations.Test.class).description();

        if (description != null && !description.isEmpty()) {
            ExtentReportManager.createTest(testName, description);
        } else {
            ExtentReportManager.createTest(testName);
        }

        ExtentReportManager.logInfo("Test Started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.logPass("Test Passed: " + result.getMethod().getMethodName());
        ExtentReportManager.handleTestResult(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentReportManager.logFail("Test Failed: " + result.getMethod().getMethodName() +
                                   ". Error: " + result.getThrowable().getMessage());
        ExtentReportManager.handleTestResult(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportManager.logSkip("Test Skipped: " + result.getMethod().getMethodName());
        ExtentReportManager.handleTestResult(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not commonly used, but can be implemented if needed
    }

    @Override
    public void onStart(ITestContext context) {
        // Can be used for additional setup if needed
    }

    @Override
    public void onFinish(ITestContext context) {
        // Can be used for additional cleanup if needed
    }
}
