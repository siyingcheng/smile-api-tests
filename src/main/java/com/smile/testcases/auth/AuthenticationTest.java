package com.smile.testcases.auth;

import com.simon.core.api.ApiResponse;
import com.smile.apiobjects.user.SmileUserDTO;
import com.smile.testcases.BaseApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.smile.apiobjects.user.SmileUsers.ADMIN;
import static com.smile.apiobjects.user.SmileUsers.INVALID;
import static com.smile.apiobjects.user.SmileUsers.OWEN;
import static com.smile.constant.JsonPathConstant.DATA_PATH;
import static com.smile.constant.JsonPathConstant.DATA_TOKEN_PATH;
import static com.smile.constant.JsonPathConstant.DATA_USERINFO_EMAIL_PATH;
import static com.smile.constant.JsonPathConstant.DATA_USERINFO_PATH;
import static com.smile.constant.JsonPathConstant.DATA_USERINFO_USERNAME_PATH;
import static com.smile.constant.JsonPathConstant.MESSAGE_PATH;
import static com.smile.testcases.auth.ErrorMessages.USERNAME_PASSWORD_INCORRECT;
import static com.smile.testcases.auth.ErrorMessages.USER_DISABLED;
import static java.net.HttpURLConnection.HTTP_OK;

public class AuthenticationTest extends BaseApiTest {
    private static final String NONE_EXIST_USERNAME = "notExistUser";
    private static final String INCORRECT_PASSWORD = "IncorrectPassWord";

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        super.beforeClass();
        generateApiDriver();
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0001: Verify Login Success")
    public void test_TC0001_VerifyLoginSuccess() {
        reporter.logStep("Step 1 - Login with a valid administrator user");
        ApiResponse response = getApiDriver().login(ADMIN);
        verifyHttpStatus(response, HTTP_OK, "Verify response status should be 200");
        assertion.assertFalse(response.getStringFromJsonPath(DATA_TOKEN_PATH).isEmpty(),
                "The authorization token should be returned in the response.");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_USERINFO_USERNAME_PATH), ADMIN.getUsername(),
                "User information, such as the username, should be returned in the response.");

        reporter.logStep("Step 2 - Login with a valid normal user");
        response = getApiDriver().login(OWEN);
        verifyHttpStatus(response, HTTP_OK, "Verify response status should be 200");
        assertion.assertFalse(response.getStringFromJsonPath(DATA_TOKEN_PATH).isEmpty(),
                "The authorization token should be returned in the response.");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_USERINFO_USERNAME_PATH), OWEN.getUsername(),
                "User information, such as the username, should be returned in the response.");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0002: Verify Login fail when username or password not correct")
    public void test_TC0002_VerifyLoginFailWhenUsernameOrPasswordNotCorrect() {
        reporter.logStep("Step 1 - Login with a none exist username");
        ApiResponse response = getApiDriver().login(NONE_EXIST_USERNAME, ADMIN.getPassword());
        verifyHttpStatus(response, USERNAME_PASSWORD_INCORRECT.getStatusCode(), "Verify response status should be 401");
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH), USERNAME_PASSWORD_INCORRECT.getMessage());
        assertion.assertEquals(response.getStringFromJsonPath(DATA_PATH), USERNAME_PASSWORD_INCORRECT.getData());

        reporter.logStep("Step 2 - Login with a exist username but invalid password");
        response = getApiDriver().login(ADMIN.getUsername(), INCORRECT_PASSWORD);
        verifyHttpStatus(response, USERNAME_PASSWORD_INCORRECT.getStatusCode(), "Verify response status should be 401");
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH), USERNAME_PASSWORD_INCORRECT.getMessage());
        assertion.assertEquals(response.getStringFromJsonPath(DATA_PATH), USERNAME_PASSWORD_INCORRECT.getData());
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0003: Verify Login fail when user is disabled")
    public void test_TC0003_VerifyLoginFailWhenUserIsDisabled() {
        reporter.logStep("Step 1 - Login with a disabled user");
        ApiResponse response = getApiDriver().login(INVALID);
        verifyHttpStatus(response, USER_DISABLED.getStatusCode(), "Verify response status should be 401");
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH), USER_DISABLED.getMessage());
        assertion.assertEquals(response.getStringFromJsonPath(DATA_PATH), USER_DISABLED.getData());
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0004: Verify Login with email and password success")
    public void test_TC0004_VerifyLoginWithEmailAndPasswordSuccess() {
        reporter.logStep("Step 1 - Login with email and password");
        ApiResponse response = getApiDriver().login(OWEN.getEmail(), OWEN.getPassword());
        verifyHttpStatus(response, HTTP_OK, "Verify response status should be 200");
        assertion.assertFalse(response.getStringFromJsonPath(DATA_TOKEN_PATH).isEmpty(),
                "The authorization token should be returned in the response.");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_USERINFO_USERNAME_PATH), OWEN.getUsername(),
                "Username should be returned in the response.");
        assertion.assertEquals(response.getStringFromJsonPath(DATA_USERINFO_EMAIL_PATH), OWEN.getEmail(),
                "Email should be returned in the response.");

        // Verify DTO
        SmileUserDTO actualUser = response.getObjectFromJsonPath(DATA_USERINFO_PATH, SmileUserDTO.class);
        SmileUserDTO expectedUer = SmileUserDTO.builder()
                .username(OWEN.getUsername())
                .email(OWEN.getEmail())
                .enabled(OWEN.getEnabled())
                .roles(OWEN.getRole())
                .build();
        verifyResponseUserInfo(actualUser, expectedUer);
    }

    private void verifyResponseUserInfo(SmileUserDTO actualUser, SmileUserDTO expectedUer) {
        assertion.assertEquals(actualUser.username(), expectedUer.username(), "Verify username in userInfo is correct");
        assertion.assertEquals(actualUser.email(), expectedUer.email(), "Verify email in userInfo is correct");
        assertion.assertEquals(actualUser.enabled(), expectedUer.enabled(), "Verify enabled in userInfo is correct");
        assertion.assertEquals(actualUser.roles(), expectedUer.roles(), "Verify roles in userInfo is correct");
    }
}
