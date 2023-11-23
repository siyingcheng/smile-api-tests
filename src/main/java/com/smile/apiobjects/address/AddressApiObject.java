package com.smile.apiobjects.address;

import com.simon.core.api.ApiResponse;
import com.simon.core.apidriver.ApiDriver;
import com.smile.apiobjects.BaseApiObject;

public class AddressApiObject extends BaseApiObject {
    private static final String USER_ID_ADDRESS_URL = BASE_URL + "/users/{userId}/address";
    private static final String USERS_ID_ADDRESS_ID_URL = USER_ID_ADDRESS_URL + "/{addressId}";
    private static final String ADDRESS_ID_URL = BASE_URL + "/users/address/{addressId}";

    public AddressApiObject(ApiDriver apiDriver) {
        super(apiDriver);
    }

    public ApiResponse getAddress(String addressId) {
        initParameters();
        setPathParameters("addressId", addressId);
        return apiDriver.get(ADDRESS_ID_URL);
    }

    public ApiResponse getAddressList(String userId) {
        initParameters();
        setPathParameters("userId", userId);
        return apiDriver.get(USER_ID_ADDRESS_URL);
    }

    public ApiResponse createAddress(String userId, AddressPayloadGenerator addressGenerator) {
        initParameters();
        setPathParameters("userId", userId);
        return apiDriver.post(USER_ID_ADDRESS_URL, addressGenerator.buildPayload());
    }

    public ApiResponse updateAddress(String userId, String addressId, AddressPayloadGenerator addressGenerator) {
        initParameters();
        setPathParameters("userId", userId, "addressId", addressId);
        return apiDriver.put(USERS_ID_ADDRESS_ID_URL, addressGenerator.buildPayload());
    }

    public ApiResponse deleteAddress(String addressId) {
        initParameters();
        setPathParameters("addressId", addressId);
        return apiDriver.delete(ADDRESS_ID_URL);
    }
}
