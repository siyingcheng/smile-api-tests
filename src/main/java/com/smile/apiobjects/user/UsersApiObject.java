package com.smile.apiobjects.user;

import com.simon.core.api.ApiResponse;
import com.simon.core.apidriver.ApiDriver;
import com.smile.apiobjects.BaseApiObject;

public class UsersApiObject extends BaseApiObject {
    private static final String BASE_USERS_URL = BASE_URL + "/users";
    private static final String USERS_ID_URL = BASE_USERS_URL + "/{id}";
    private static final String CURRENT_USER_URL = BASE_USERS_URL + "/current_user";

    public UsersApiObject(ApiDriver apiDriver) {
        super(apiDriver);
    }

    public ApiResponse getUser(String userId) {
        initParameters();
        setPathParameters("id", userId);
        return apiDriver.get(USERS_ID_URL);
    }

    public ApiResponse getUserList() {
        initParameters();
        return apiDriver.get(BASE_USERS_URL);
    }

    public ApiResponse createUser(UserPayloadGenerator userPayload) {
        initParameters();
        return apiDriver.post(BASE_USERS_URL, userPayload.buildPayload());
    }

    public ApiResponse deleteUser(String userId) {
        initParameters();
        setPathParameters("id", userId);
        return apiDriver.delete(USERS_ID_URL);
    }

    public ApiResponse updateUser(String userId, UserPayloadGenerator newUserPayload) {
        initParameters();
        setPathParameters("id", userId);
        return apiDriver.put(USERS_ID_URL, newUserPayload.buildPayload());
    }

    public ApiResponse getCurrentUser() {
        initParameters();
        return apiDriver.get(CURRENT_USER_URL);
    }
}
