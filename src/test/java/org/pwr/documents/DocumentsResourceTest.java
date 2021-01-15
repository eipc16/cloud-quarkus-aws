package org.pwr.documents;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.pwr.domain.documents.DocumentsResource;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static io.restassured.RestAssured.given;


@QuarkusTest
@TestHTTPEndpoint(DocumentsResource.class)
public class DocumentsResourceTest {

    private String getToken() throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();

        JsonObject AuthParameters = new JsonObject();
        AuthParameters.put("USERNAME", "przemek");
        AuthParameters.put("PASSWORD", "Przemek123321@#");
        JsonObject body = new JsonObject();
        body.put("AuthParameters", AuthParameters);
        body.put("AuthFlow", "USER_PASSWORD_AUTH");
        body.put("ClientId", "2a96ivgsgnuunq9fpolfevronm");

        var request = HttpRequest.newBuilder(
                URI.create("https://cognito-idp.us-east-1.amazonaws.com"))
                .header("X-Amz-Target", "AWSCognitoIdentityProviderService.InitiateAuth")
                .header("Content-Type", "application/x-amz-json-1.1")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject responseBody = new JsonObject(response.body());

        return responseBody.getJsonObject("AuthenticationResult").getString("AccessToken");
    }

    @Test
    public void testSearchDocumentsEndpointUnauthorized() {
        given().when().get().then().statusCode(401);
    }

    @Test
    public void testSearchDocumentsEndpointAuthorized() throws IOException, InterruptedException {
        given().header("Authorization", "Bearer " + this.getToken())
                .when().get().then().statusCode(200);
    }
}
