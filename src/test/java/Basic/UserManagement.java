package Basic;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * End-to-end workflow test for User Management API
 *
 * Workflow Steps:
 * 1. Admin Login              – authenticates and captures authToken
 * 2. Register New User (Group A) – registers user in group 1deae17a-c67a-4bb0-bdeb-df0fc9e2e526
 * 3. Approve User Registration – admin approves the new user
 * 4. Make User Admin          – promotes user to admin role
 * 5. Login with New Admin User – verifies admin role
 * 6. Delete Newly Created User – cleanup
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Management API - Complete Workflow Test")
public class UserManagementTest {

    // ----------------------------------------------------------------
    // Shared state across tests
    // ----------------------------------------------------------------
    private static String authToken;
    private static String registeredUserId;
    private static String registeredEmail;
    private static String newAdminToken;

    // ----------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------
    private static final String BASE_URI  = "https://www.ndosiautomation.co.za";
    private static final String BASE_PATH = "/APIDEV";

    private static final String ADMIN_EMAIL    = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "@12345678";

    private static final String NEW_USER_PASSWORD = "@87654321";
    // Group A ID as specified
    private static final String GROUP_ID = "1deae17a-c67a-4bb0-bdeb-df0fc9e2e526";

    // ----------------------------------------------------------------
    // Setup
    // ----------------------------------------------------------------
    @BeforeAll
    static void setup() {
        RestAssured.baseURI  = BASE_URI;
        RestAssured.basePath = BASE_PATH;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Generate unique email for this test run
        registeredEmail = "testuser+" +
                UUID.randomUUID().toString().replace("-", "").substring(0, 10) +
                "@ndositest.com";

        System.out.println("=== Starting User Management Workflow Test ===");
        System.out.println("Generated test email: " + registeredEmail);
    }

    // ----------------------------------------------------------------
    // Step 1: Admin Login
    // ----------------------------------------------------------------
    @Test
    @Order(1)
    @DisplayName("Step 1: Admin Login - Authenticate and get token")
    void step1_AdminLogin() {
        String requestBody = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                ADMIN_EMAIL, ADMIN_PASSWORD
        );

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.token", notNullValue())
                .extract()
                .response();

        authToken = response.jsonPath().getString("data.token");
        System.out.println("[Step 1] ✓ Admin logged in, token: " +
                authToken.substring(0, 20) + "...");
    }

    // ----------------------------------------------------------------
    // Step 2: Register New User in Group A
    // ----------------------------------------------------------------
    @Test
    @Order(2)
    @DisplayName("Step 2: Register New User - Create user in Group A")
    void step2_RegisterNewUser() {
        String requestBody = String.format(
                "{" +
                        "\"firstName\":\"Test\"," +
                        "\"lastName\":\"User\"," +
                        "\"email\":\"%s\"," +
                        "\"password\":\"%s\"," +
                        "\"confirmPassword\":\"%s\"," +
                        "\"groupId\":\"%s\"" +
                        "}",
                registeredEmail, NEW_USER_PASSWORD, NEW_USER_PASSWORD, GROUP_ID
        );

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/register")
                .then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("data.id", notNullValue())
                .extract()
                .response();

        registeredUserId = response.jsonPath().getString("data.id");
        System.out.println("[Step 2] ✓ User registered:");
        System.out.println("       User ID: " + registeredUserId);
        System.out.println("       Email: " + registeredEmail);
        System.out.println("       Group: " + GROUP_ID + " (Group A)");
    }

    // ----------------------------------------------------------------
    // Step 3: Approve User Registration
    // ----------------------------------------------------------------
    @Test
    @Order(3)
    @DisplayName("Step 3: Approve User - Admin approves new user")
    void step3_ApproveUserRegistration() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .put("/admin/users/{userId}/approve", registeredUserId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));

        System.out.println("[Step 3] ✓ User " + registeredUserId + " approved");
    }

    // ----------------------------------------------------------------
    // Step 4: Make User Admin
    // ----------------------------------------------------------------
    @Test
    @Order(4)
    @DisplayName("Step 4: Make User Admin - Promote to admin role")
    void step4_MakeUserAdmin() {
        String requestBody = "{\"role\":\"admin\"}";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .body(requestBody)
                .when()
                .put("/admin/users/{userId}/role", registeredUserId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));

        System.out.println("[Step 4] ✓ User " + registeredUserId + " promoted to admin");
    }

    // ----------------------------------------------------------------
    // Step 5: Login with New Admin User & Verify Role
    // ----------------------------------------------------------------
    @Test
    @Order(5)
    @DisplayName("Step 5: Verify Admin - Login and confirm admin role")
    void step5_VerifyNewAdminUser() {
        String requestBody = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                registeredEmail, NEW_USER_PASSWORD
        );

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                // FIX: role is inside data.user, not directly in data
                .body("data.user.role", equalTo("admin"))
                .extract()
                .response();

        newAdminToken = response.jsonPath().getString("data.token");
        String role = response.jsonPath().getString("data.user.role");
        String userId = response.jsonPath().getString("data.user.id");
        String approvalStatus = response.jsonPath().getString("data.user.approvalStatus");

        System.out.println("[Step 5] ✓ New user login successful");
        System.out.println("       User ID: " + userId);
        System.out.println("       Role verified: " + role);
        System.out.println("       Approval Status: " + approvalStatus);
        System.out.println("       Group: " + response.jsonPath().getString("data.user.groupName"));
        System.out.println("       Token: " + newAdminToken.substring(0, 20) + "...");
    }
    // ----------------------------------------------------------------
    // Step 6: Delete Newly Created User (Cleanup)
    // ----------------------------------------------------------------
    @Test
    @Order(6)
    @DisplayName("Step 6: Cleanup - Delete the test user")
    void step6_DeleteNewlyCreatedUser() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete("/admin/users/{userId}", registeredUserId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));

        System.out.println("[Step 6] ✓ User " + registeredUserId + " deleted");
        System.out.println("=== WORKFLOW COMPLETED SUCCESSFULLY ===");
    }
}