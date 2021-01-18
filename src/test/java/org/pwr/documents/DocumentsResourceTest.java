package org.pwr.documents;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pwr.domain.documents.*;
import org.pwr.infrastructure.dynamodb.DynamoPage;
import org.pwr.infrastructure.dynamodb.DynamoPaginable;

import javax.inject.Inject;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestHTTPEndpoint(DocumentsResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DocumentsResourceTest {

    @Inject
    DocumentsService documentsService;

    @Inject
    DocumentMapper documentMapper;

    String token;

    @BeforeAll
    void setUp() throws IOException, InterruptedException {
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

        token = responseBody.getJsonObject("AuthenticationResult").getString("AccessToken");
    }

    @Test
    public void testSearchDocumentsEndpointAuthorized() throws IOException {
        DynamoPage<DocumentDTO> dynamoPage = documentsService.getDocuments(new DynamoPaginable(), new DocumentSearchFilter()).mapTo(documentMapper::toDTO);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dynamoPage);

        given().header("Authorization", "Bearer " + token).when().get().then().statusCode(200).body(is(json));
    }

    @Test
    public void testUploadDocumentEndpointAuthorized() throws IOException {
        given().header("Authorization", "Bearer " + token)
                .param("fileName", "test.jpg")
                .param("mimeType", "image/jpeg")
                .param("name", "123")
                .param("sourceLang", "en")
                .param("targetLang", "pl")
                .multiPart("file", new File("src/test/java/org/pwr/documents/test.jpg"))
                .when().post().then().statusCode(200);
    }
}
