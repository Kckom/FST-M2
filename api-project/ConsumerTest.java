package LiveProject;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@ExtendWith(PactConsumerTestExt.class)
public class ConsumerTest {
//Headers

    Map<String , String> reqheaders = new HashMap<>();
    //API resource path
    String resourcePath = "/api/users";

    //creating pact
    @Pact(consumer = "UserConsumer" , provider = "UserProvider")
    public RequestResponsePact createPact(PactDslWithProvider builder){
        //set the headers
        reqheaders.put("Content-Type" , "application/json");

        //create request and response body
        DslPart reqResBody = new PactDslJsonBody()
                .numberType("id")
                .stringType("firstName")
                .stringType("lastName")
                .stringType("email");
        return builder.given("Request to create a user")
                .uponReceiving("Request to create a user")
                .method("POST")
                .path(resourcePath)
                .headers(reqheaders)
                .body(reqResBody)
                .willRespondWith()
                .status(201)
                .body(reqResBody)
                .toPact();
    }
    @Test
    @PactTestFor(providerName = "UserProvider" , port = "8282")
    public void consumerTest(){
        String baseURI = "http://localhost:8282";
        //define req body
        Map<String , Object> reqBody = new HashMap<>();
        reqBody.put("id" , 123);
        reqBody.put("firstName" , "Komal");
        reqBody.put("lastName" , "Chauhan");
        reqBody.put("email" , "komal@example.com");

        Response response =  given().headers(reqheaders).body(reqBody)
                 .when().post(baseURI + resourcePath);
        //print the response
        System.out.println(response.getBody().asPrettyString());
        response.then().statusCode(201);

    }


}
