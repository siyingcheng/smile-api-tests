package com.smile.testcases.user;

import com.simon.core.api.ApiResponse;
import com.smile.apiobjects.address.AddressApiObject;
import com.smile.apiobjects.address.AddressDTO;
import com.smile.apiobjects.address.AddressPayloadGenerator;
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
import static com.smile.constant.JsonPathConstant.DATA_FULL_ADDRESS;
import static com.smile.constant.JsonPathConstant.DATA_ID_PATH;
import static com.smile.constant.JsonPathConstant.DATA_IS_DEFAULT;
import static com.smile.constant.JsonPathConstant.DATA_PATH;
import static com.smile.constant.JsonPathConstant.DATA_PHONE;
import static com.smile.constant.JsonPathConstant.MESSAGE_PATH;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class AddressAPiTest extends BaseApiTest {
    private UsersApiObject usersApiObject;
    private AddressApiObject addressApiObject;
    private final List<String> userIds = new ArrayList<>();
    private final List<String> addressIds = new ArrayList<>();
    private String userId;
    private String addressId;
    private AddressPayloadGenerator addressPayloadGenerator;
    private final String uniqueNumber = getUniqueNumberStr(5);
    private final String username = "User" + uniqueNumber;
    private static final String NON_EXIST_ID = String.valueOf(Integer.MAX_VALUE);

    @Override
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        super.beforeClass();

        generateApiDriver();
        usersApiObject = new UsersApiObject(getApiDriver());
        addressApiObject = new AddressApiObject(getApiDriver());

        reporter.logStep("SETUP - Login with an administrator");
        ApiResponse response = getApiDriver().login(ADMIN);
        verifyStatusIsOK(response);

        reporter.logStep("SETUP - Create a new user");
        UserPayloadGenerator newUserPayload = UserPayloadGenerator.builder()
                .username(username)
                .email(username + "@example.com")
                .nickname(username + " TC")
                .password(DEFAULT_PASSWORD)
                .build();
        response = usersApiObject.createUser(newUserPayload);
        verifyStatusIsOK(response);
        userId = response.getStringFromJsonPath(DATA_ID_PATH);
        userIds.add(userId);

        reporter.logStep("SETUP - Create an address for user: " + username);
        addressPayloadGenerator = AddressPayloadGenerator.builder()
                .fullAddress(getFaker().address().fullAddress())
                .phone(getFaker().phoneNumber().cellPhone())
                .build();
        response = addressApiObject.createAddress(userId, addressPayloadGenerator);
        verifyStatusIsOK(response);
        addressId = response.getStringFromJsonPath(DATA_ID_PATH);
        addressIds.add(addressId);

        getApiDriver().logout();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        deleteAddresses(addressIds);
        deleteUsers(userIds);

        reporter.logStep("CLEANUP - Logout administrator");
        getApiDriver().logout();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        reporter.logStep("SETUP - Login with: " + username);
        ApiResponse response = getApiDriver().login(username, DEFAULT_PASSWORD);
        verifyStatusIsOK(response);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        getApiDriver().logout();
    }

    @Test(groups = {"P0"}, description = "TC0017: Verify find address by id success")
    public void test_TC0017_VerifyFindAddressByIdSuccess() {
        reporter.logStep("Step 1 - Retrieve address with id: " + addressId);
        ApiResponse response = addressApiObject.getAddress(addressId);
        verifyStatusIsOK(response);
        assertion.assertNotNull(response.getObjectFromJsonPath(DATA_ID_PATH, Integer.class),
                "Verify id returned in the response");
        assertion.assertFalse(response.getStringFromJsonPath(DATA_FULL_ADDRESS).isEmpty(),
                "Verify fullAddress returned in the response");
        assertion.assertFalse(response.getStringFromJsonPath(DATA_PHONE).isEmpty(),
                "Verify phone returned in the response");
        assertion.assertNotNull(response.getObjectFromJsonPath(DATA_IS_DEFAULT, Boolean.class),
                "Verify isDefault returned in the response");
    }

    @Test(groups = {"Regression"}, description = "TC:0018 Verify find address by id error when id non-exist")
    public void test_TC0018_VerifyFindAddressByIdErrorWhenIdNonExist() {
        reporter.logStep("Step 1 - Retrieve address with non-exist id: " + NON_EXIST_ID);
        ApiResponse response = addressApiObject.getAddress(NON_EXIST_ID);
        verifyHttpStatus(response, HTTP_NOT_FOUND);
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH),
                "Not found fullAddress with ID: " + NON_EXIST_ID,
                "Verify error message is returned in the response");
    }

    @Test(groups = {"P0"}, description = "TC0019: Verify find address list success")
    public void test_TC0019_VerifyFindAddressListSuccess() {
        reporter.logStep("Step 1 - Retrieve address list");
        ApiResponse response = addressApiObject.getAddressList(userId);
        verifyStatusIsOK(response);
        List<AddressDTO> addressDTOs = response.getListFromJsonPath(DATA_PATH, AddressDTO.class);
        assertion.assertFalse(addressDTOs.isEmpty(), "Verify address list is not empty");
    }

    @Test(groups = {"P0"}, description = "TC0019: Verify create address for user success")
    public void test_TC0020_VerifyCreateAddressForUserSuccess() {
        reporter.logStep("Step 1 - Create an address for user: " + username);
        AddressPayloadGenerator addressPayloadGenerator = AddressPayloadGenerator.builder()
                .fullAddress(getFaker().address().fullAddress())
                .phone(getFaker().phoneNumber().cellPhone())
                .build();
        ApiResponse response = addressApiObject.createAddress(userId, addressPayloadGenerator);
        verifyStatusIsOK(response);
        String addressId = response.getStringFromJsonPath(DATA_ID_PATH);
        addressIds.add(addressId);
        AddressDTO responseDto = response.getObjectFromJsonPath(DATA_PATH, AddressDTO.class);
        assertion.assertEquals(responseDto.fullAddress(), addressPayloadGenerator.getFullAddress(),
                "Verify full address is correct in response");
        assertion.assertEquals(responseDto.phone(), addressPayloadGenerator.getPhone(),
                "Verify phone is correct in response");
        assertion.assertFalse(responseDto.isDefault(), "Verify isDefault is correct in response");
    }

    @Test(groups = {"Regression"}, description = "TC:0021 Verify create address for user error when user non-exist")
    public void test_TC0021_VerifyCreateAddressForUserErrorWhenUserNonExist() {
        reporter.logStep("Step 1 - Create an address for non-exist user");
        ApiResponse response = addressApiObject.createAddress(NON_EXIST_ID, addressPayloadGenerator);
        verifyHttpStatus(response, HTTP_NOT_FOUND);
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH),
                "Not found user with ID: " + NON_EXIST_ID,
                "Verify error message is returned in the response");
    }


    @Test(groups = {"Regression"}, description = "TC:0022 Verify create address for user error when properties incorrect")
    public void test_TC0022_VerifyCreateAddressForUserErrorWhenPropertiesIncorrect() {
        reporter.logStep("Step 1 - Create an address for user without full address");
        ApiResponse response = addressApiObject.createAddress(userId, AddressPayloadGenerator.builder()
                .phone(getFaker().phoneNumber().cellPhone())
                .build());
        verifyHttpStatus(response, HTTP_BAD_REQUEST);
        assertion.assertEquals(response.getStringFromJsonPath(DATA_FULL_ADDRESS),
                "fullAddress is required",
                "Verify error message is returned in the response");

        reporter.logStep("Step 2 - Create an address for user without phone");
        response = addressApiObject.createAddress(userId, AddressPayloadGenerator.builder()
                .fullAddress(getFaker().address().fullAddress())
                .build());
        verifyHttpStatus(response, HTTP_BAD_REQUEST);
        assertion.assertEquals(response.getStringFromJsonPath(DATA_PHONE),
                "phone is required",
                "Verify error message is returned in the response");
    }

    @Test(groups = {"P0"}, description = "TC0023: Verify update address for user success")
    public void test_TC0023_VerifyUpdateAddressForUserSuccess() {
        reporter.logStep("Step 1 - Update an address for user: " + username);
        addressPayloadGenerator = AddressPayloadGenerator.builder()
                .fullAddress(getFaker().address().fullAddress())
                .phone(getFaker().phoneNumber().cellPhone())
                .build();
        ApiResponse response = addressApiObject.updateAddress(userId, addressId, addressPayloadGenerator);
        verifyStatusIsOK(response);
        AddressDTO responseDto = response.getObjectFromJsonPath(DATA_PATH, AddressDTO.class);
        assertion.assertEquals(responseDto.fullAddress(), addressPayloadGenerator.getFullAddress(),
                "Verify full address is correct in response");
        assertion.assertEquals(responseDto.phone(), addressPayloadGenerator.getPhone(),
                "Verify phone is correct in response");
        assertion.assertFalse(responseDto.isDefault(), "Verify isDefault is correct in response");
    }

    @Test(groups = {"Regression"}, description = "TC:0024 Verify update address for user error when user non-exist")
    public void test_TC0024_VerifyUpdateAddressForUserErrorWhenUserNonExist() {
        reporter.logStep("Step 1 - Update an address for non-exist user");
        ApiResponse response = addressApiObject.updateAddress(NON_EXIST_ID, addressId, addressPayloadGenerator);
        verifyHttpStatus(response, HTTP_NOT_FOUND);
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH),
                "Not found user with ID: " + NON_EXIST_ID,
                "Verify error message is returned in the response");
    }

    @Test(groups = {"Regression"}, description = "TC:0025 Verify update address for user error when address non-exist")
    public void test_TC0025_VerifyUpdateAddressForUserErrorWhenUserNonExist() {
        reporter.logStep("Step 1 - Update an address for non-exist address");
        ApiResponse response = addressApiObject.updateAddress(userId, NON_EXIST_ID, addressPayloadGenerator);
        verifyHttpStatus(response, HTTP_NOT_FOUND);
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH),
                "Not found address with ID: " + NON_EXIST_ID,
                "Verify error message is returned in the response");
    }

    @Test(groups = {"Regression"}, description = "TC:0026 Verify update address for user error when properties incorrect")
    public void test_TC0026_VerifyUpdateAddressForUserErrorWhenPropertiesIncorrect() {
        reporter.logStep("Step 1 - Update an address for user without full address");
        ApiResponse response = addressApiObject.updateAddress(userId, addressId, AddressPayloadGenerator.builder()
                .phone(getFaker().phoneNumber().cellPhone())
                .build());
        verifyHttpStatus(response, HTTP_BAD_REQUEST);
        assertion.assertEquals(response.getStringFromJsonPath(DATA_FULL_ADDRESS),
                "fullAddress is required",
                "Verify error message is returned in the response");

        reporter.logStep("Step 2 - Update an address for user without phone");
        response = addressApiObject.updateAddress(userId, addressId, AddressPayloadGenerator.builder()
                .fullAddress(getFaker().address().fullAddress())
                .build());
        verifyHttpStatus(response, HTTP_BAD_REQUEST);
        assertion.assertEquals(response.getStringFromJsonPath(DATA_PHONE),
                "phone is required",
                "Verify error message is returned in the response");
    }

    @Test(groups = {"P0"}, description = "TC0027: Verify delete address for user success")
    public void test_TC0027_VerifyDeleteAddressForUserSuccess() {
        reporter.logStep("Step 1 - Create an address for user: " + username);
        ApiResponse response = addressApiObject.createAddress(userId, AddressPayloadGenerator.builder()
                .fullAddress(getFaker().address().fullAddress())
                .phone(getFaker().phoneNumber().cellPhone())
                .build());
        verifyStatusIsOK(response, "Verify create address success");
        String addressId = response.getStringFromJsonPath(DATA_ID_PATH);

        reporter.logStep("Step 2 - Delete address with ID: " + addressId);
        response = addressApiObject.deleteAddress(addressId);
        verifyStatusIsOK(response);

        reporter.logStep("Step 3 - Retrieve user's address list, check the address should be deleted");
        response = addressApiObject.getAddressList(userId);
        verifyStatusIsOK(response);
        List<AddressDTO> addressDTOs = response.getListFromJsonPath(DATA_PATH, AddressDTO.class);
        assertion.assertTrue(addressDTOs.stream()
                        .noneMatch(addressDTO -> addressDTO.id().equals(Integer.valueOf(addressId))),
                "Verify the address should not be shown in the response.");
    }

    @Test(groups = {"Regression"}, description = "TC:0028 Verify delete address for user error when user non-exist")
    public void test_TC0028_VerifyDeleteAddressForUserErrorWhenUserNonExist() {
        reporter.logStep("Step 1 - Delete an address with non-exist ID: " + NON_EXIST_ID);
        ApiResponse response = addressApiObject.deleteAddress(NON_EXIST_ID);
        verifyHttpStatus(response, HTTP_NOT_FOUND);
        assertion.assertEquals(response.getStringFromJsonPath(MESSAGE_PATH),
                "Not found address with ID: " + NON_EXIST_ID,
                "Verify error message is returned in the response");
    }
}
