package examples;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class Activity3 {

    //specification
    RequestSpecification reqspec;
    ResponseSpecification respspec;
    int petId;

    @BeforeClass
    public void setup()
    {
        reqspec = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/pet")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "token")
                .build();
        respspec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectResponseTime(lessThan(3000L), TimeUnit.MILLISECONDS)
                .expectBody("status", equalTo("alive"))
                //.expectBody("name", equalTo("Riley"))
                .build();

    }

    @DataProvider
    public Object[][] petInfoProvider() {
        // Setting parameters to pass to test case
        Object[][] testData = new Object[][] {
                { 77232, "Riley", "alive" },
                { 77233, "Hansel", "alive" }
        };
        return testData;
    }

    @Test(priority=1)
    public void addNewPet(){
        String reqBody = "{\"id\": 77232, \"name\": \"Riley\", \"status\": \"alive\"}";
        //Generate Response
        Response response = given().spec(reqspec)
                .body(reqBody)
                .when().post();

        reqBody = "{\"id\": 77233, \"name\": \"Hansel\", \"status\": \"alive\"}";
        response = given().spec(reqspec) // Use requestSpec
                .body(reqBody) // Send request body
                .when().post(); // Send POST request
        response.then().spec(respspec);
    }

    @Test(dataProvider = "petInfoProvider" , priority=2)
    public void getPet(int id, String name , String status) {
        Response response = given().spec(reqspec)
                .pathParam("petId", id)
                .when().get("/{petId}");
        System.out.println(response.asPrettyString());
        response.then().spec(respspec)
                .body("name" , equalTo(name));
    }
        @Test(dataProvider = "petInfoProvider" , priority=3)
        public void removePet (int id, String name , String status) {
            Response response = given().spec(reqspec)
                    .pathParam("petId", id)
                    .when().delete("/{petId}");
        response.then().body("code" , equalTo(200));


        }

}
