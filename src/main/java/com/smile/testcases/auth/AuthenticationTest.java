package com.smile.testcases.auth;

import com.smile.core.api.ApiResponse;
import com.smile.core.testng.BaseApiTest;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

@Slf4j
public class AuthenticationTest extends BaseApiTest {

    @Test(groups = {"P0", "Regression"}, description = "TC0001: Verify Login Success")
    public void test_TC0001_VerifyLoginSuccess() {
        reporter.logStep("Step 1 - Login with a valid user");
        ApiResponse response = getApiDriver().login("admin", "PassW0rd");
        assertions.assertEquals(response.statusCode(), HTTP_OK, "Verify login success when username and password correct");
    }

    @Test(groups = {"P0", "Regression"}, description = "TC0002: Verify Login fail when username or password not correct")
    public void test_TC0002_VerifyLoginFailWhenUsernameOrPasswordNotCorrect() {
        reporter.logStep("Step 1 - Login with a none exist username");
        ApiResponse response = getApiDriver().login("notExistUser", "PassW0rd");
        assertions.assertEquals(response.statusCode(), HTTP_OK, "Verify Login fail when username none exist");

        reporter.logStep("Step 2 - Login with a exist username but invalid password");
        response = getApiDriver().login("admin", "NotCorrectPassWord");
        assertions.assertEquals(response.statusCode(), HTTP_UNAUTHORIZED, "Verify Login fail when password incorrect");
    }
}
