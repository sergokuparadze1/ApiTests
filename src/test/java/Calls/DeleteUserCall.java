package Calls;

import Utils.Config;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class DeleteUserCall {
    public Response DeleteUserCall(int id){
        return  given()
                .when()
                .delete(Config.baseUrl + "/" + id) ;
    }
}
