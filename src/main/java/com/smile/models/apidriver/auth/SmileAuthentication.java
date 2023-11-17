package com.smile.models.apidriver.auth;

import com.smile.core.api.ApiResponse;
import com.smile.core.config.ConfigKeys;
import com.smile.core.config.Configurator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Base64;

import static com.smile.core.reporter.ReportFactor.reportRequestHeader;
import static com.smile.core.reporter.ReportFactor.reportRequestUrl;
import static com.smile.core.reporter.ReportFactor.reportResponseBody;
import static com.smile.core.reporter.ReportFactor.reportResponseHeaders;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

public record SmileAuthentication(Configurator configurator) implements IAuthentication {
    private static final String DEFAULT_LOGIN_URL = "/api/v1/login";

    @Override
    public ApiResponse login(String username, String password) {
        String loginUrl = configurator.getBackendUrl() + configurator.getParameterOrDefault(ConfigKeys.LOGIN_URI, DEFAULT_LOGIN_URL);
        RequestSpecification request = given()
                .header(AUTHORIZATION, getBasicToken(username, password));
        reportRequestUrl("POST", loginUrl);
        reportRequestHeader(request, true);
        Response response = request.post(loginUrl);
        reportResponseHeaders(response, true);
        reportResponseBody(response, true);
        return ApiResponse.of(response);
    }

    private String getBasicToken(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
