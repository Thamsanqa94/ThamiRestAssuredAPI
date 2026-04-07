package Basic;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.github.javafaker.Faker;



public class UserRegistration {

    static String authToken;
    static String userId;
    static String registeredEmail;
    static String baseURL = "https://ndosiautomation.co.za";

    @Test
    public void adminLoginTest() {
        String apiPath = "/APIDEV/login";
        String payload = "{\n" +
                "    \"email\": \"spare@admin.com\",\n" +
                "    \"password\": \"@12345678\"\n" +
                "}";

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(payload)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Status code should be 200");
        authToken = response.jsonPath().getString("data.token");
    }

    @Test(priority = 2)
    public void registerUser() {
        String apiPath = "/APIDEV/register";
        registeredEmail = Faker.instance().internet().emailAddress();
        String payload = String.format("{\n" +
                "    \"firstName\": \"dsfdsa\",\n" +
                "    \"lastName\": \"sdfdsaf\",\n" +
                "    \"email\": \"%s\",\n" +
                "    \"password\": \"Agric123@\",\n" +
                "    \"confirmPassword\": \"Agric123@\",\n" +
                "    \"phone\": \"\",\n" +
                "    \"groupId\": \"1f49eb00-ff0d-476c-8ecc-6ff2fb53a35c\"\n" +
                "}", registeredEmail);

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(payload)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 201, "Status code should be 201");
        userId = response.jsonPath().getString("data.id");
    }

@Test(priority = 3)
public void approveUserRegistration(){

    String apiPath = "/APIDEV/admin/users/"+ UserRegistration.userId+"/approve";

    Response response = RestAssured.given()
            .baseUri(UserRegistration.baseURL)
            .basePath(apiPath)
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + UserRegistration.authToken)
            .log().all()
            .put().prettyPeek();

    int actualStatusCode = response.getStatusCode();
    Assert.assertEquals(actualStatusCode, 200, "Status code should be 200");

}
@Test(priority = 4)
public void userLoginTest() {

    String apiPath = "/APIDEV/login";
    String payload = String.format( "{\n" +
            "    \"email\": \"%s\",\n" +
            "    \"password\": \"Agric123@\"\n" +
            "}", UserRegistration.registeredEmail);

    Response response = RestAssured.given()
            .baseUri(UserRegistration.baseURL)
            .basePath(apiPath)
            .header("Content-Type", "application/json")
            .body(payload)
            .log().all()
            .post().prettyPeek();

    int actualStatusCode = response.getStatusCode();
    Assert.assertEquals(actualStatusCode, 200, "Status code should be 200");

}


}


