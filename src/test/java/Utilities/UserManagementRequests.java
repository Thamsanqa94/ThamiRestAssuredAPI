package Utilities;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * HTTP Request Utility for User Management API
 * Provides reusable methods for GET, POST, PUT, DELETE with/without authentication
 */
public class UserManagementRequests {

        public static RequestSpecification getRequestSpec() {
            return RestAssured.given()
                    .contentType(ContentType.JSON)
                    .urlEncodingEnabled(false);
        }

        public static RequestSpecification getRequestSpecWithAuth(String token) {
            return getRequestSpec()
                    .header("Authorization", "Bearer " + token);
        }

        // GET
        public static Response get(String url) {
            Response response = getRequestSpec()
                    .when()
                    .get(url)
                    .then()
                    .extract()
                    .response();
            response.getBody().prettyPrint();
            return response;
        }

        public static Response getWithAuth(String url, String token) {
            Response response = getRequestSpecWithAuth(token)
                    .when()
                    .get(url)
                    .then()
                    .extract()
                    .response();
            response.getBody().prettyPrint();
            return response;
        }

        // POST
        public static Response post(String url, Object body) {
            Response response = getRequestSpec()
                    .body(body)
                    .when().log().all()
                    .post(url)
                    .then()
                    .extract()
                    .response();
            response.getBody().prettyPrint();
            return response;
        }

        public static Response postWithAuth(String url, Object body, String token) {
            Response response = getRequestSpecWithAuth(token)
                    .body(body)
                    .when().log().all()
                    .post(url)
                    .then()
                    .extract()
                    .response();
            response.getBody().prettyPrint();
            return response;
        }

        // PUT
        public static Response put(String url, Object body) {
            Response response = getRequestSpec()
                    .body(body)
                    .when()
                    .put(url)
                    .then()
                    .extract()
                    .response();
            response.getBody().prettyPrint();
            return response;
        }

        public static Response putWithAuth(String url, Object body, String token) {
            Response response = getRequestSpecWithAuth(token)
                    .body(body)
                    .when()
                    .put(url)
                    .then()
                    .extract()
                    .response();
            response.getBody().prettyPrint();
            return response;
        }

        // DELETE
        public static Response delete(String url) {
            Response response = getRequestSpec()
                    .when()
                    .delete(url)
                    .then()
                    .extract()
                    .response();
            response.getBody().prettyPrint();
            return response;
        }

        public static Response deleteWithAuth(String url, String token) {
            Response response = getRequestSpecWithAuth(token)
                    .when()
                    .delete(url)
                    .then()
                    .extract()
                    .response();
            response.getBody().prettyPrint();
            return response;
        }
}