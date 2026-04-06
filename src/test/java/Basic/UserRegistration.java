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
                "    \"email\": \"tk@gmail.com\",\n" +
                "    \"password\": \"Agric123@\"\n" +
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
                "    \"password\": \"@a12345678\",\n" +
                "    \"confirmPassword\": \"@a12345678\",\n" +
                "    \"phone\": \"\",\n" +
                "    \"groupId\": \"5328c91e-fc40-11f0-8e00-5000e6331276\"\n" +
                "}", registeredEmail);

        Response response = RestAssured.given()
                .baseUri(baseURL)
                .basePath(apiPath)
                .header("Content-Type", "application/json")
                .body(payload)
                .log().all()
                .post().prettyPeek();

        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, 200, "Status code should be 200");
        userId = response.jsonPath().getString("data.id");
    }
}


