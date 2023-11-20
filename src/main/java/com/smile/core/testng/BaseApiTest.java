package com.smile.core.testng;

import com.smile.core.api.ApiResponse;
import com.smile.core.apidriver.ApiDriver;
import com.smile.core.apidriver.auth.SmileAuthentication;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static java.net.HttpURLConnection.HTTP_OK;

@Slf4j
@Getter
public class BaseApiTest extends BaseTest {
    private ApiDriver apiDriver;

    protected void generateApiDriver() {
        if (apiDriver == null) {
            apiDriver = new ApiDriver(new SmileAuthentication(getConfigurator()));
        }
    }

    public void verifyStatusIsOK(ApiResponse response) {
        verifyHttpStatus(response, HTTP_OK, "Verify http status is 200");
    }

    public void verifyStatusIsOK(ApiResponse response, String message) {
        verifyHttpStatus(response, HTTP_OK, message);
    }

    public void verifyHttpStatus(ApiResponse response, int expectedStatus, String message) {
        assertion.assertEquals(response.statusCode(), expectedStatus, message);
    }
}
