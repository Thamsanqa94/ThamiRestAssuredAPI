package Utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.testng.ITestResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReports utility class for API test reporting
 */
public class ExtentReportManager {

    private static ExtentReports extentReports;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    /**
     * Initialize ExtentReports
     */
    public static void initReports() {
        if (extentReports == null) {
            extentReports = new ExtentReports();

            // Create timestamp for unique report names
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReports/API_Test_Report_" + timestamp + ".html";

            // Ensure the directory exists
            try {
                java.nio.file.Files.createDirectories(java.nio.file.Paths.get(reportPath).getParent());
            } catch (Exception e) {
                System.err.println("Failed to create report directory: " + e.getMessage());
            }

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

            // Configure the report
            sparkReporter.config().setDocumentTitle("API Test Report");
            sparkReporter.config().setReportName("Thami RestAssured API Tests");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

            extentReports.attachReporter(sparkReporter);

            // Add system information
            extentReports.setSystemInfo("Environment", "QA");
            extentReports.setSystemInfo("User", System.getProperty("user.name"));
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        }
    }

    /**
     * Create a new test in the report
     */
    public static void createTest(String testName) {
        ExtentTest test = extentReports.createTest(testName);
        extentTest.set(test);
    }

    /**
     * Create a new test with description
     */
    public static void createTest(String testName, String description) {
        ExtentTest test = extentReports.createTest(testName, description);
        extentTest.set(test);
    }

    /**
     * Get current test instance
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Check if test is created
     */
    public static boolean isTestCreated() {
        return extentTest.get() != null;
    }

    /**
     * Log test info
     */
    public static void logInfo(String message) {
        if (isTestCreated()) {
            getTest().log(Status.INFO, message);
        } else {
            System.out.println("[INFO] " + message);
        }
    }

    /**
     * Log test pass
     */
    public static void logPass(String message) {
        if (isTestCreated()) {
            getTest().log(Status.PASS, message);
        } else {
            System.out.println("[PASS] " + message);
        }
    }

    /**
     * Log test fail
     */
    public static void logFail(String message) {
        if (isTestCreated()) {
            getTest().log(Status.FAIL, message);
        } else {
            System.err.println("[FAIL] " + message);
        }
    }

    /**
     * Log test skip
     */
    public static void logSkip(String message) {
        if (isTestCreated()) {
            getTest().log(Status.SKIP, message);
        } else {
            System.out.println("[SKIP] " + message);
        }
    }

    /**
     * Log API request details
     */
    public static void logApiRequest(RequestSpecification requestSpec, String endpoint) {
        if (!isTestCreated()) {
            System.out.println("[WARN] Cannot log API request - test not initialized. Call createTest() first.");
            return;
        }

        try {
            QueryableRequestSpecification queryableRequest = SpecificationQuerier.query(requestSpec);

            StringBuilder requestDetails = new StringBuilder();
            requestDetails.append("<b>API Request Details:</b><br>");
            requestDetails.append("<b>Method:</b> ").append(queryableRequest.getMethod()).append("<br>");
            requestDetails.append("<b>URI:</b> ").append(queryableRequest.getURI()).append("<br>");
            requestDetails.append("<b>Base URI:</b> ").append(queryableRequest.getBaseUri()).append("<br>");
            requestDetails.append("<b>Base Path:</b> ").append(queryableRequest.getBasePath()).append("<br>");
            requestDetails.append("<b>Endpoint:</b> ").append(endpoint).append("<br>");

            if (queryableRequest.getHeaders() != null && queryableRequest.getHeaders().size() > 0) {
                requestDetails.append("<b>Headers:</b><br>");
                for (io.restassured.http.Header header : queryableRequest.getHeaders()) {
                    String value = header.getValue() != null ? header.getValue().toString() : "null";
                    String name = header.getName();
                    String headerLine = "&nbsp;&nbsp;" + name + ": " + value + "<br>";
                    requestDetails.append((CharSequence) headerLine);
                }
            }

            if (queryableRequest.getBody() != null) {
                requestDetails.append("<b>Request Body:</b><br><pre>").append((String) queryableRequest.getBody()).append("</pre><br>");
            }

            getTest().log(Status.INFO, requestDetails.toString());
        } catch (Exception e) {
            getTest().log(Status.WARNING, "Could not log request details: " + e.getMessage());
        }
    }

    /**
     * Log API response details
     */
    public static void logApiResponse(Response response) {
        if (!isTestCreated()) {
            System.out.println("[WARN] Cannot log API response - test not initialized. Call createTest() first.");
            return;
        }

        StringBuilder responseDetails = new StringBuilder();
        responseDetails.append("<b>API Response Details:</b><br>");
        responseDetails.append("<b>Status Code:</b> ").append(response.getStatusCode()).append("<br>");
        responseDetails.append("<b>Status Line:</b> ").append(response.getStatusLine()).append("<br>");
        responseDetails.append("<b>Response Time:</b> ").append(response.getTime()).append(" ms<br>");

        if (response.getHeaders() != null && response.getHeaders().size() > 0) {
            responseDetails.append("<b>Response Headers:</b><br>");
            for (io.restassured.http.Header header : response.getHeaders()) {
                String value = header.getValue() != null ? header.getValue().toString() : "null";
                String name = header.getName();
                String headerLine = "&nbsp;&nbsp;" + name + ": " + value + "<br>";
                responseDetails.append((CharSequence) headerLine);
            }
        }

        if (response.getBody() != null && response.getBody().asString().length() > 0) {
            responseDetails.append("<b>Response Body:</b><br><pre>").append(response.getBody().asPrettyString()).append("</pre><br>");
        }

        // Color code based on status
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            getTest().log(Status.PASS, responseDetails.toString());
        } else if (response.getStatusCode() >= 400) {
            getTest().log(Status.FAIL, responseDetails.toString());
        } else {
            getTest().log(Status.WARNING, responseDetails.toString());
        }
    }

    /**
     * Add screenshot or attachment (for future use)
     */
    public static void addAttachment(String name, String path) {
        if (isTestCreated()) {
            getTest().addScreenCaptureFromPath(path, name);
        }
    }

    /**
     * Flush the report
     */
    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    /**
     * Handle test result for TestNG integration
     */
    public static void handleTestResult(ITestResult result) {
        if (getTest() == null) return;

        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                getTest().log(Status.PASS, "Test passed successfully");
                break;
            case ITestResult.FAILURE:
                getTest().log(Status.FAIL, "Test failed: " + result.getThrowable());
                break;
            case ITestResult.SKIP:
                getTest().log(Status.SKIP, "Test skipped: " + result.getThrowable());
                break;
        }
    }
}