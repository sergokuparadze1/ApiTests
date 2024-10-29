package Calls;

import Utils.Config;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class GetUserCall {
    public Response GetUserCall(int userId){
        return  given()
                .when()
                .get(Config.baseUrl + "/" + userId) ;
    }
}
