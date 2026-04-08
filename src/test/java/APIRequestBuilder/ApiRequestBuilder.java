package APIRequestBuilder;

import Utilities.ExtentReportManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * API Request Builder for User Management API
 * Provides static methods that return Response objects for direct chaining
 */
public class ApiRequestBuilder {

    private static String authToken;
    private static String registeredUserId;

    private static final String BASE_URI = "https://www.ndosiautomation.co.za";
    private static final String BASE_PATH = "/APIDEV";

    static {
        RestAssured.baseURI = BASE_URI;
        RestAssured.basePath = BASE_PATH;
    }

    private static RequestSpecification getBaseSpec() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .urlEncodingEnabled(false);
    }

    private static RequestSpecification getAuthSpec() {
        return getBaseSpec()
                .header("Authorization", "Bearer " + authToken);
    }

    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void setRegisteredUserId(String userId) {
        registeredUserId = userId;
    }

    public static String getRegisteredUserId() {
        return registeredUserId;
    }

    public static Response loginUserResponse(String email, String password) {
        String requestBody = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                email, password
        );

        RequestSpecification requestSpec = getBaseSpec().body(requestBody);
        ExtentReportManager.logApiRequest(requestSpec, "/login");

        Response response = requestSpec
                .when().log().all()
                .post("/login")
                .then()
                .extract()
                .response();

        ExtentReportManager.logApiResponse(response);

        if (response.statusCode() == 200 && response.jsonPath().getBoolean("success")) {
            authToken = response.jsonPath().getString("data.token");
            ExtentReportManager.logInfo("Auth token captured successfully");
        }
        return response;
    }

    public static Response registerUserResponse(String firstName, String lastName,
                                                String email, String password,
                                                String groupId) {
        String requestBody = String.format(
                "{" +
                        "\"firstName\":\"%s\"," +
                        "\"lastName\":\"%s\"," +
                        "\"email\":\"%s\"," +
                        "\"password\":\"%s\"," +
                        "\"confirmPassword\":\"%s\"," +
                        "\"groupId\":\"%s\"" +
                        "}",
                firstName, lastName, email, password, password, groupId
        );

        RequestSpecification requestSpec = getBaseSpec().body(requestBody);
        ExtentReportManager.logApiRequest(requestSpec, "/register");

        Response response = requestSpec
                .when().log().all()
                .post("/register")
                .then()
                .extract()
                .response();

        ExtentReportManager.logApiResponse(response);

        if (response.statusCode() == 201 && response.jsonPath().getBoolean("success")) {
            registeredUserId = response.jsonPath().getString("data.id");
            ExtentReportManager.logInfo("User registered successfully with ID: " + registeredUserId);
        }
        return response;
    }

    public static Response approveUserRegistrationResponse() {
        return approveUserRegistrationResponse(registeredUserId);
    }

    public static Response approveUserRegistrationResponse(String userId) {
        RequestSpecification requestSpec = getAuthSpec();
        ExtentReportManager.logApiRequest(requestSpec, "/admin/users/" + userId + "/approve");

        Response response = requestSpec
                .when()
                .put("/admin/users/" + userId + "/approve")
                .then()
                .extract()
                .response();

        ExtentReportManager.logApiResponse(response);
        response.getBody().prettyPrint();
        return response;
    }

    public static Response updateUserRoleResponse(String role) {
        return updateUserRoleResponse(registeredUserId, role);
    }

    public static Response updateUserRoleResponse(String userId, String role) {
        String requestBody = String.format("{\"role\":\"%s\"}", role);

        RequestSpecification requestSpec = getAuthSpec().body(requestBody);
        ExtentReportManager.logApiRequest(requestSpec, "/admin/users/" + userId + "/role");

        Response response = requestSpec
                .when()
                .put("/admin/users/" + userId + "/role")
                .then()
                .extract()
                .response();

        ExtentReportManager.logApiResponse(response);
        response.getBody().prettyPrint();
        return response;
    }

    public static Response deleteUserResponse() {
        return deleteUserResponse(registeredUserId);
    }

    public static Response deleteUserResponse(String userId) {
        RequestSpecification requestSpec = getAuthSpec();
        ExtentReportManager.logApiRequest(requestSpec, "/admin/users/" + userId);

        Response response = requestSpec
                .when()
                .delete("/admin/users/" + userId)
                .then()
                .extract()
                .response();

        ExtentReportManager.logApiResponse(response);
        response.getBody().prettyPrint();
        return response;
    }

    public static Response makeUserAdminResponse() {
        return updateUserRoleResponse("admin");
    }
}