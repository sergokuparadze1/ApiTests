package Calls;

import Models.UserPostPutRequestModel;
import Utils.Config;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PutUserCall {
    public Response CreateUserCall(UserPostPutRequestModel userPostPutRequestModel, int userId){
        return  given()
                .when()
                .header("Content-Type", "application/json")
                .header("Accept", "*/*")
                //.header("Accept-Encoding", "gzip, deflate, br")
                .body(userPostPutRequestModel)
                .put(Config.baseUrl + "/" + userId) ;
    }
}
