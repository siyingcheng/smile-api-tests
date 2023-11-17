package com.smile.models.apidriver.auth;

import com.smile.core.api.ApiResponse;
import com.smile.core.config.Configurator;
import io.restassured.response.Response;

public interface IAuthentication {
    ApiResponse login(String username, String password);

    Configurator configurator();
}
