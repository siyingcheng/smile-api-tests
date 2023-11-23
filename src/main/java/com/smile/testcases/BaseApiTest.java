package com.smile.testcases;

import com.github.javafaker.Faker;
import com.simon.core.api.ApiResponse;
import com.simon.core.apidriver.ApiDriver;
import com.simon.core.apidriver.auth.SmileAuthentication;
import com.simon.core.testng.BaseTest;
import com.smile.apiobjects.address.AddressApiObject;
import com.smile.apiobjects.user.UsersApiObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;

import java.util.List;

import static com.smile.apiobjects.user.SmileUsers.ADMIN;
import static java.net.HttpURLConnection.HTTP_OK;

@Slf4j
@Getter
public class BaseApiTest extends BaseTest {
    private ApiDriver apiDriver;
    private final Faker faker = new Faker();

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        initTest();
    }

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

    public void verifyHttpStatus(ApiResponse response, int expectedStatus) {
        assertion.assertEquals(response.statusCode(), expectedStatus, "Verify http status code is " + expectedStatus);
    }

    public void deleteUsers(List<String> userIds) {
        if (userIds.isEmpty()) return;

        loginAdmin();

        UsersApiObject usersApiObject = new UsersApiObject(getApiDriver());
        for (String userId : userIds) {
            reporter.logStep("CLEANUP - Delete user with ID: " + userId);
            ApiResponse response = usersApiObject.deleteUser(userId);
            verifyStatusIsOK(response);
        }
    }

    public void deleteAddresses(List<String> addressIds) {
        if (addressIds.isEmpty()) return;

        loginAdmin();

        AddressApiObject addressApiObject = new AddressApiObject(getApiDriver());
        for (String addressId : addressIds) {
            reporter.logStep("CLEANUP - Delete address with ID: " + addressId);
            ApiResponse response = addressApiObject.deleteAddress(addressId);
            verifyStatusIsOK(response);
        }
    }

    public void loginAdmin() {
        reporter.logStep("CLEANUP - Login with: " + ADMIN.getUsername());
        ApiResponse loginResponse = getApiDriver().login(ADMIN);
        verifyStatusIsOK(loginResponse);
    }
}
