package Calls;

import Models.UserPostPutRequestModel;
import Utils.Config;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PostUserCall {
    public Response CreateUserCall(UserPostPutRequestModel userPostPutRequestModel){
        return  given()
                .when()
                .body(userPostPutRequestModel)
                .post(Config.baseUrl) ;
    }
}
