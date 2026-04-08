package Test;

import APIRequestBuilder.ApiRequestBuilder;
import com.github.javafaker.Faker;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserManagementTest {

    static String registeredEmail;

    @Test
    public void adminLoginTest() {
        // Remove the instantiation - use static method directly
        ApiRequestBuilder.loginUserResponse("admin@gmail.com", "@12345678")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(dependsOnMethods = "adminLoginTest")
    public void registerNewUser() {
        registeredEmail = Faker.instance().internet().emailAddress();
        ApiRequestBuilder.registerUserResponse("New", "User", registeredEmail, "@87654321", "1deae17a-c67a-4bb0-bdeb-df0fc9e2e526")
                .then()
                .log().all()
                .assertThat()
                .statusCode(201)
                .body("success", equalTo(true));
    }

    @Test(dependsOnMethods = "registerNewUser")
    public void approveUserRegistration() {
        ApiRequestBuilder.approveUserRegistrationResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(dependsOnMethods = "approveUserRegistration")
    public void makeUserAdmin() {
        ApiRequestBuilder.makeUserAdminResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test(dependsOnMethods = "makeUserAdmin")
    public void verifyNewAdminUser() {
        ApiRequestBuilder.loginUserResponse(registeredEmail, "@87654321")
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.user.role", equalTo("admin"));
    }

    @Test(dependsOnMethods = "verifyNewAdminUser")
    public void deleteNewlyCreatedUser() {
        // Re-login as original admin before delete
        ApiRequestBuilder.loginUserResponse("admin@gmail.com", "@12345678");

        ApiRequestBuilder.deleteUserResponse()
                .then()
                .log().all()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }
}