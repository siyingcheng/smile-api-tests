package com.smile.testcases.user;

import com.simon.core.api.ApiResponse;
import com.smile.apiobjects.user.SmileRole;
import com.smile.apiobjects.user.SmileUserDTO;
import com.smile.apiobjects.user.UserPayloadGenerator;
import com.smile.apiobjects.user.UsersApiObject;
import com.smile.testcases.BaseApiTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.smile.apiobjects.user.IUser.DEFAULT_PASSWORD;
import static com.smile.apiobjects.user.SmileUsers.ADMIN;
import static com.smile.constant.JsonPathConstant.DATA_EMAIL;
import static com.smile.constant.JsonPathConstant.DATA_ENABLED;
import static com.smile.constant.JsonPathConstant.DATA_ID_PATH;
import static com.smile.constant.JsonPathConstant.DATA_NICKNAME;
import static com.smile.constant.JsonPathConstant.DATA_PATH;
import static com.smile.constant.JsonPathConstant.DATA_ROLES;
import static com.smile.constant.JsonPathConstant.DATA_USERNAME;
import static com.smile.constant.JsonPathConstant.MESSAGE_PATH;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class UsersApiTest extends BaseApiTest {
    private UsersApiObject usersApiObject;
    private final List<String> userIds = new ArrayList<>();
    private String newUserID;
    private final String uniqueNumber = getUniqueNumberStr(5);
    private UserPayloadGenerator newUserPayload;
    private static final String NON_EXIST_ID = String.valueOf(Integer.MAX_VALUE);

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        super.beforeClass();

        generateApiDriver();
        usersApiObject = new UsersApiObject(getApiDriver());

        reporter.logStep("SETUP - Login with an administrator");
        ApiResponse response = getApiDriver().login(ADMIN);
        verifyStatusIsOK(response);

        reporter.logStep("SETUP - Create a new user");
        String username = "User" + uniqueNumber;
        newUserPayload = UserPayloadGenerator.builder()
                .username(username)
                .email(username + "@example.com")
                .nickname(username + " TC")
                .password(DEFAULT_PASSWORD)
                .build();
        response = usersApiObject.createUser(newUserPayload);
        verifyStatusIsOK(response);
        newUserID = response.getStringFromJsonPath(DATA_ID_PATH);
        userIds.add(newUserID);

        getApiDriver().logout();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        deleteUsers(userIds);

        reporter.logStep("CLEANUP - Logout administrator");
        getApiDriver().logout();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        reporter.logStep("SETUP - Login with: " + ADMIN.getUsername());
        ApiResponse response = getApiDriver().login(ADMIN);
        verifyStatusIsOK(response);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        getApiDriver().logout();
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0005: Verify retrieve user by ID success")
    public void test_TC0005_VerifyRetrieveUserByIDSuccess() {
        reporter.logStep("Step 1 - Retrieve user with ID: " + newUserID);
        ApiResponse response = usersApiObject.getUser(newUserID);
        verifyStatusIsOK(response);
        SmileUserDTO responseObject = response.getObjectFromJsonPath(DATA_PATH, SmileUserDTO.class);
        SmileUserDTO expectedObject = SmileUserDTO.builder()
                .username(newUserPayload.getUsername())
                .email(newUserPayload.getEmail())
                .roles(SmileRole.ROLE_USER)
                .enabled(true)
                .build();
        verifyUserDTO(responseObject, expectedObject);
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0006: Verify retrieve user by ID error when ID none exist")
    public void test_TC0006_VerifyRetrieveUserByIDErrorWhenIdNoneExist() {
        String noneExistID = String.valueOf(Integer.MAX_VALUE);

        reporter.logStep("Step 1 - Retrieve user with ID: " + noneExistID);
        ApiResponse response = usersApiObject.getUser(noneExistID);
        assertion.assertEquals(response.statusCode(), HTTP_NOT_FOUND, "Verify status code should be 404");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0007: Verify retrieve user list success")
    public void test_TC0007_VerifyRetrieveUserListSuccess() {
        reporter.logStep("Step 1 - Retrieve user list");
        ApiResponse response = usersApiObject.getUserList();
        verifyStatusIsOK(response, "Verify retrieve user list success");
        List<SmileUserDTO> userDTOList = response.getListFromJsonPath(DATA_PATH, SmileUserDTO.class);
        assertion.assertFalse(userDTOList.isEmpty(), "Verify user list in response");
        assertion.assertNotNull(response.getFromJsonPath("data.find { it.username == '" + ADMIN.getUsername() + "'} "), "Verify user returned in user list");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0008: Verify create user success")
    public void test_TC0008_VerifyCreateUserSuccess() {
        reporter.logStep("Step 1 - Create user");
        UserPayloadGenerator userPayload = UserPayloadGenerator.builder()
                .username("test_tc0008")
                .nickname("test_tc0008 TC")
                .email("test_tc0008@example.com")
                .password("Is#TestPassW0rd")
                .build();
        ApiResponse response = usersApiObject.createUser(userPayload);
        verifyStatusIsOK(response);
        userIds.add(response.getStringFromJsonPath(DATA_ID_PATH));

        assertion.assertEquals(response.getStringFromJsonPath(DATA_USERNAME), "test_tc0008", "Verify username returned success");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_NICKNAME), "test_tc0008 TC", "Verify nickname returned success");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_EMAIL), "test_tc0008@example.com", "Verify email returned success");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_ROLES), SmileRole.ROLE_USER.name(), "Verify roles returned success");
        assertion.assertEquals(response.getFromJsonPath(DATA_ENABLED), true, "Verify roles returned success");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0009: Verify create user error when properties invalid")
    public void test_TC0009_VerifyCreateUserErrorWhenPropertiesInvalid() {
        reporter.logStep("Step 1 - Create user without username");
        UserPayloadGenerator userPayload = UserPayloadGenerator.builder()
                .nickname("test_tc0009 TC")
                .email("test_tc0009@example.com")
                .password("Is#TestPassW0rd")
                .build();
        ApiResponse response = usersApiObject.createUser(userPayload);
        verifyHttpStatus(response, HTTP_BAD_REQUEST, "Verify status code should be 400");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_USERNAME), "username is required",
                "Verify error reason returned in response");

        reporter.logStep("Step 2 - Create user invalid length username");
        userPayload.setUsername("ab");
        response = usersApiObject.createUser(userPayload);
        verifyHttpStatus(response, HTTP_BAD_REQUEST, "Verify status code should be 400");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_USERNAME), "username length must between 3 and 16",
                "Verify error reason returned in response");

        reporter.logStep("Step 3 - Create user without email");
        userPayload.setUsername("test_tc0009")
                .setEmail(null);
        response = usersApiObject.createUser(userPayload);
        verifyHttpStatus(response, HTTP_BAD_REQUEST, "Verify status code should be 400");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_EMAIL), "email is required",
                "Verify error reason returned in response");

        reporter.logStep("Step 4 - Create user with invalid email");
        userPayload.setEmail("test_tc0009");
        response = usersApiObject.createUser(userPayload);
        verifyHttpStatus(response, HTTP_BAD_REQUEST, "Verify status code should be 400");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_EMAIL), "email format is invalid",
                "Verify error reason returned in response");

        reporter.logStep("Step 5 - Create user with invalid password");
        userPayload.setEmail("test_tc0009@example.com")
                .setPassword("TEST00009");
        response = usersApiObject.createUser(userPayload);
        verifyHttpStatus(response, HTTP_BAD_REQUEST, "Verify status code should be 400");
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH),
                "Password is not strong enough; 1. At least a number; 2. A least a lower letter; 3. At least a upper letter; 4. No spaces; 5. At least 8 characters, at most 20 characters",
                "Verify error reason returned in response");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0010: Verify create user error when properties duplicate")
    public void test_TC0010_VerifyCreateUserErrorWhenPropertiesDuplicate() {
        reporter.logStep("Step 1 - Create user with duplicate username");
        UserPayloadGenerator userPayload = UserPayloadGenerator.builder()
                .username(ADMIN.getUsername())
                .nickname("test_tc0010 TC")
                .email("test_tc0010@example.com")
                .password("Is#TestPassW0rd")
                .build();
        ApiResponse response = usersApiObject.createUser(userPayload);
        verifyHttpStatus(response, HTTP_BAD_REQUEST, "Verify status code should be 400");
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH), "username already exists",
                "Verify error reason returned in response");

        reporter.logStep("Step 2 - Create user with duplicate email");
        userPayload.setUsername("test_tc0010")
                .setEmail(ADMIN.getEmail());
        response = usersApiObject.createUser(userPayload);
        verifyHttpStatus(response, HTTP_BAD_REQUEST, "Verify status code should be 400");
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH), "email already exists",
                "Verify error reason returned in response");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0011: Verify update user success")
    public void test_TC0011_VerifyUpdateUserSuccess() {
        reporter.logStep("Step 1 - Retrieve user with ID: " + newUserID);
        ApiResponse response = usersApiObject.getUser(newUserID);
        verifyStatusIsOK(response);
        SmileUserDTO userDTO = response.getObjectFromJsonPath(DATA_PATH, SmileUserDTO.class);

        reporter.logStep("Step 2 - Update user with ID: " + newUserID);
        newUserPayload.setNickname("tc0011 TC")
                .setEmail("tc0011@example.com");
        UserPayloadGenerator updatePayload = UserPayloadGenerator.of(userDTO)
                .setNickname("tc0011 TC")
                .setEmail("tc0011@example.com");
        response = usersApiObject.updateUser(newUserID, updatePayload);
        verifyStatusIsOK(response);
        assertion.assertEquals(response.getStringFromJsonPath(DATA_NICKNAME), "tc0011 TC", "Verify nickname returned success");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_EMAIL), "tc0011@example.com", "Verify email returned success");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0012: Verify update user error when id none-exist")
    public void test_TC0012_VerifyUpdateUserErrorWhenIdNoneExist() {
        reporter.logStep("Step 1 - Update user with non-exist ID: " + NON_EXIST_ID);
        ApiResponse response = usersApiObject.updateUser(NON_EXIST_ID, newUserPayload);
        verifyHttpStatus(response, HTTP_NOT_FOUND, "Verify status code should be 400");
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH),
                "Not found user with ID: " + NON_EXIST_ID,
                "Verify error message returned in response");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0013: Verify update user error when properties invalid")
    public void test_TC0013_VerifyUpdateUserErrorWhenPropertiesInvalid() {
        reporter.logStep("Step 1 - Retrieve user with ID: " + newUserID);
        ApiResponse response = usersApiObject.getUser(newUserID);
        verifyStatusIsOK(response);
        SmileUserDTO userDTO = response.getObjectFromJsonPath(DATA_PATH, SmileUserDTO.class);

        reporter.logStep("Step 2 - Update user with invalid email");
        UserPayloadGenerator updatePayload = UserPayloadGenerator.of(userDTO)
                .setEmail("tc0013");
        response = usersApiObject.updateUser(NON_EXIST_ID, updatePayload);
        verifyHttpStatus(response, HTTP_BAD_REQUEST, "Verify status code should be 400");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_EMAIL),
                "email format is invalid",
                "Verify error message returned in response");

        reporter.logStep("Step 2 - Update user with invalid password");
        updatePayload = UserPayloadGenerator.of(userDTO)
                .setEmail("tc0013@example.com")
                .setPassword("abc");
        response = usersApiObject.updateUser(NON_EXIST_ID, updatePayload);
        verifyHttpStatus(response, HTTP_BAD_REQUEST, "Verify status code should be 400");
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH),
                "Password is not strong enough; 1. At least a number; 2. A least a lower letter; 3. At least a upper letter; 4. No spaces; 5. At least 8 characters, at most 20 characters",
                "Verify error message returned in response");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0014: Verify delete user success")
    public void test_TC0014_VerifyDeleteUserSuccess() {
        reporter.logStep("Step 1 - Create a user");
        String username = "TC0014" + uniqueNumber;
        UserPayloadGenerator userPayload = UserPayloadGenerator.builder()
                .username(username)
                .email(username + "@example.com")
                .nickname(username + " TC")
                .password(DEFAULT_PASSWORD)
                .build();
        ApiResponse response = usersApiObject.createUser(userPayload);
        verifyStatusIsOK(response);
        newUserID = response.getStringFromJsonPath(DATA_ID_PATH);

        reporter.logStep("Step 2 - Delete user with ID: " + newUserID);
        response = usersApiObject.deleteUser(newUserID);
        verifyStatusIsOK(response);
        assertion.assertEquals(response.getMessage(), "Delete user success",
                "Verify message returned in response");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0015: Verify delete user error when ID non-exist")
    public void test_TC0015_VerifyDeleteUserErrorWhenIdNonExist() {
        reporter.logStep("Step 1 - Delete user with ID: " + NON_EXIST_ID);
        ApiResponse response = usersApiObject.deleteUser(NON_EXIST_ID);
        verifyHttpStatus(response, HTTP_NOT_FOUND, "Verify status code should be 404");
        assertion.assertEquals(response.getMessage(), "Not found user with ID: " + NON_EXIST_ID,
                "Verify error message returned in response");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0016: Verify retrieve current user success")
    public void test_TC0016_VerifyDeleteUserErrorWhenIdNonExist() {
        reporter.logStep("Step 1 - Login with username: " + newUserPayload.getUsername());
        ApiResponse response = getApiDriver().login(newUserPayload.getUsername(), DEFAULT_PASSWORD);
        verifyStatusIsOK(response);

        reporter.logStep("Step 2 - Retrieve current user");
        response = usersApiObject.getCurrentUser();
        verifyStatusIsOK(response);
        assertion.assertEquals(response.getStringFromJsonPath(DATA_USERNAME), newUserPayload.getUsername(),
                "Verify user information returned in response");
    }

    private void verifyUserDTO(SmileUserDTO responseObject, SmileUserDTO expectedObject) {
        assertion.assertEquals(responseObject.username(), expectedObject.username(), "Verify user's username in response");
        assertion.assertEquals(responseObject.email(), expectedObject.email(), "Verify user's email in response");
        assertion.assertEquals(responseObject.roles(), expectedObject.roles(), "Verify user's roles in response");
        assertion.assertEquals(responseObject.enabled(), expectedObject.enabled(), "Verify user's enabled in response");
    }
}
