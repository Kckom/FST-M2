package LiveProject;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class GitHubTest {

    RequestSpecification reqspec;
    ResponseSpecification respspec;
    String sshKey;
    int SSHId;

    @BeforeClass
    public void setup(){
        reqspec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "token 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")
                .setBaseUri("https://api.github.com")
                .build();
        respspec = new ResponseSpecBuilder()
                .expectResponseTime(lessThan(3000L), TimeUnit.MILLISECONDS)
                  .expectBody("title", equalTo("TestAPIKey"))
                .expectBody("verified", equalTo("true"))
                .build();
        sshKey = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCrYdI8QxDeleKv+VBAk1D6iSFO2mm5ujYOqbD6CPlQE9SrTVb0kmdzTW4xRE14YJwMVf/MUuW8cFjGik/C+n87ggtPTUameGwpP0TC4JorHwZcjZBoQyAUCHNsyS6YZqYTIEgR2+y+4DwJxWHT9JpiJMv/bB6PaQsCvjEPLbyjPVL96orD53CA4R36J+deT8/0FJkXO44/pekj0Q88byjoWTDefy68b8BBDIDeo7HP+v4zVzFt8+RNm2x7wZPXNUCCRNua25LspVXMwu8cKJTnNtlg6UVkumloBaapRDBtOfftANi46ungwv2YtvZGLxKIq6IViOmoUeogLn5TlhcV";
    }

    @Test(priority = 0)
    public void addSSHToken(){
        String reqBody = "{\"title\": \"TestAPIKey\",\"key\": \"" + sshKey + "\"}";
        //Generate Response
        Response response = given().spec(reqspec)
                .body(reqBody)
                .when().post("/user/keys");

        String resBody = response.getBody().asPrettyString();
        System.out.println(resBody);
        SSHId = response.then().extract().path("id");
        System.out.println(SSHId);
        response.then().statusCode(201);
    }

    @Test(priority = 1)
    public void getKey(){

        Response response = given().spec(reqspec)
                .when().get("/user/keys");

        String resBody = response.getBody().asPrettyString();
        System.out.println(resBody);
//        String Key = response.then().extract().path("key");
//        System.out.println(Key);
         response.then().statusCode(200);
    }

    @Test(priority = 2)
    public void deleteKey(){
        Response response = given().spec(reqspec)
                .pathParam("keyId" , SSHId)
                .when().delete("/user/keys/{keyId}");
        String resBody = response.getBody().asPrettyString();
        System.out.println(resBody);
        response.then().statusCode(204);
    }

}
