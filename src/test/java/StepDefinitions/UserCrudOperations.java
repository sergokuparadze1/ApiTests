package StepDefinitions;

import Calls.DeleteUserCall;
import Calls.PostUserCall;
import Calls.GetUserCall;
import Calls.PutUserCall;
import Models.PutUserResponseModel;
import Models.UserPostPutRequestModel;
import Models.GetUserResponseModel;
import Utils.Config;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserCrudOperations {
    private Response response;
    GetUserResponseModel getUserResponseModel;
    PutUserResponseModel putUserResponseModel ;

    SoftAssert softAssert = new SoftAssert() ;
    PostUserCall postUserCall = new PostUserCall();
    GetUserCall getUserCall = new GetUserCall() ;
    PutUserCall putUserCall = new PutUserCall() ;
    DeleteUserCall deleteUserCall = new DeleteUserCall() ;
    // Scenario: Create a user
    @When("send a POST request")
    public void CreateUser() {
        response = null ;
        UserPostPutRequestModel userPostPutRequestModel = new UserPostPutRequestModel();
        userPostPutRequestModel.setName("testName");
        userPostPutRequestModel.setJob("testJob");
        response = postUserCall.CreateUserCall(userPostPutRequestModel) ;
        if(response.jsonPath().get("id") != null){
            deleteUserCall.DeleteUserCall(Integer.valueOf(response.jsonPath().get("id")));
        }
        Assert.assertNotNull(response);
    }
    @Then("response status should be {int} after creation")
    public void checkStatusCode(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, "Status code mismatch for user creation");
    }
    @And("response should contain correct user data")
    public void CheckUserData() {
        int userId = Integer.valueOf(response.jsonPath().get("id"))  ;
        try{
            Map<String, Object> responseData = getUserCall.GetUserCall(userId).jsonPath().getMap("") ;
            softAssert.assertEquals(responseData.get("name"), "testName", "incorrect name");
            softAssert.assertEquals(responseData.get("job"), "testJob", "incorrect job");
        }catch (Exception ex){
            Assert.fail("Error parsing JSON");
            System.err.println("Error parsing JSON -  " + ex.getMessage());

        }
        softAssert.assertAll();
    }
    // Create a user without name field (Negative)
    @When("send a POST request without name field")
    public void CreateUserWithoutName() {
        response = null ;
        UserPostPutRequestModel userPostPutRequestModel = new UserPostPutRequestModel();
        userPostPutRequestModel.setJob("testJob");
        response = postUserCall.CreateUserCall(userPostPutRequestModel) ;
        Assert.assertNotNull(response);
    }
    @Then("response status should be {int} after creation without Name field")
    public void checkStatusCodeWithoutName(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, "Status code mismatch for user creation");
    }
    // Create a user without job field (Negative)
    @When("send a POST request without job field")
    public void CreateUserWithoutJob() {
        response = null ;
        UserPostPutRequestModel userPostPutRequestModel = new UserPostPutRequestModel();
        userPostPutRequestModel.setJob("testJob");
        response = postUserCall.CreateUserCall(userPostPutRequestModel) ;
        Assert.assertNotNull(response);
    }
    @Then("response status should be {int} after creation without job field")
    public void checkStatusCodeWithoutJob(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, "Status code mismatch for user creation");
    }

    // Scenario: Read user details
    @When("send a GET request to {string}")
    public void sendGetRequestForUser(int id) {
        response = getUserCall.GetUserCall(id);
        getUserResponseModel = response.as(GetUserResponseModel.class);
    }
    @Then("response status should be {int}")
    public void verifyStatusCodeForExistingUser(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode);
    }
    @And("response should contain the user id as {int} and response data should contains correct data")
    public void verifyUserData(int expectedId) {
        softAssert.assertEquals(getUserResponseModel.getData().getId(), expectedId, "User ID mismatch");
        softAssert.assertEquals(getUserResponseModel.getData().getFirst_name(), "Janet", "First name mismatch");
        softAssert.assertEquals(getUserResponseModel.getData().getLast_name(), "Weaver", "Last name mismatch");
        softAssert.assertEquals(getUserResponseModel.getData().getEmail(), "janet.weaver@reqres.in", "Email mismatch");
        softAssert.assertEquals(getUserResponseModel.getData().getAvatar(), "https://reqres.in/img/faces/2-image.jpg", "Avatar URL mismatch");
        softAssert.assertAll();
    }
    @And("Content-Type header should be {string}")
    public void verifyContentType(String contentType) {
        softAssert.assertEquals(response.header("Content-Type"), contentType, "Content-Type header mismatch");
        softAssert.assertAll();
    }

    // Scenario: Read details with non-existing user id (Negative)
    @When("send a GET request to non existing {string}")
    public void sendAGetRequestToNonExistingUser(String id) {
        response = given().when().get(Config.baseUrl + "/" + id);
        getUserResponseModel = null;  // Setting to null as no user should be returned
    }
    @Then("response status should be {int} for non existing user")
    public void verifyStatusCodeForNonExistingUser(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode);
    }
    @Then("response must be null")
    public void verifyNullResponseForNonExistingUser() {
        softAssert.assertNull(getUserResponseModel, "Expected response to be null for non-existing user");
        softAssert.assertAll();
    }

    // Scenario: update user
    @Given("get any user for change data {int}")
    public void getUserForUpdate(int id) {
        //მომაქვს 2 აიდით კლიენტი
        getUserResponseModel = null ;
        response = getUserCall.GetUserCall(id) ;
        getUserResponseModel = response.as(GetUserResponseModel.class);
        Assert.assertNotNull(getUserResponseModel);
    }
    @When("change and  send a PUT request {int}")
    public void changeAndSendPutRequest(int id) {
        //ვცვლი მონაცემს
        UserPostPutRequestModel userPostPutRequestModel = new UserPostPutRequestModel() ;
        userPostPutRequestModel.setName(getUserResponseModel.getData().getFirst_name() + "test");
        userPostPutRequestModel.setJob("testjob");
        response = putUserCall.CreateUserCall(userPostPutRequestModel, id) ;
        putUserResponseModel  = response.as(PutUserResponseModel.class) ;
        Assert.assertNotNull(putUserResponseModel);
    }
    @Then("response status should be {int} after update")
    public void checkUpdateStatus(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode);
    }
    @And("response must contain the updated data {int}")
    public void CheckUserData(int id) {
        //განახლების მერე ისევ მომაქვს კლიენტი და ვამოწმებ შეიცვალა თუ არა მნიშვნელობა
        Map<String, Object> responseData  =  getUserCall.GetUserCall(id).jsonPath().getMap("data") ;
        Assert.assertEquals(responseData.get("first_name"), getUserResponseModel.getData().getFirst_name() + "test" , "name mismatch");
    }

    // Scenario: Delete user
    @Given("create new user and check status {int}")
    public void CreateUser(int statusCode) {
        response = null ;
        UserPostPutRequestModel userPostPutRequestModel = new UserPostPutRequestModel();
        userPostPutRequestModel.setName("testNameForDelete");
        userPostPutRequestModel.setJob("testjobForDelete");
        response =  postUserCall.CreateUserCall(userPostPutRequestModel) ;
        Assert.assertEquals(response.getStatusCode() , statusCode);
    }
    @When("delete the user and check status code {int}")
    public void deleteUser(int statusCode) {
       Response deleteResponse =  deleteUserCall.DeleteUserCall(Integer.valueOf(response.jsonPath().get("id")));
       Assert.assertEquals(deleteResponse.getStatusCode() , statusCode);
    }
    @Then("user should not exists")
    public void CheckUserExists() {
        //ვამოწმებ არსებობს თუ არა კლიენტი
        int deletedUserId = Integer.valueOf(response.jsonPath().get("id")) ;
        Assert.assertEquals(getUserCall.GetUserCall(deletedUserId).getStatusCode() , 404) ;
    }
    // Scenario: Delete a non existent user
    @When("try delete user {int}")
    public void deleteNonExistingUser(int userId) {
        response = null ;
        response =  deleteUserCall.DeleteUserCall(userId);
    }
    @Then("response for {int} user status code should be {int}")
    public void CheckNonExistingUserStatus(int user , int statusCode) {
        // აქ აბრუნებს 204 ს ნაცვლად 404 ისა
        Assert.assertEquals(response.getStatusCode() , statusCode) ;
    }
}
