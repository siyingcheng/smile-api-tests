package com.smile.core.apidriver;

import com.smile.apiobjects.user.SmileUsers;
import com.smile.core.api.ApiResponse;
import com.smile.core.apidriver.auth.IAuthentication;
import com.smile.core.config.ConfigKeys;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.smile.core.reporter.ReportFactor.reportRequestBody;
import static com.smile.core.reporter.ReportFactor.reportRequestHeader;
import static com.smile.core.reporter.ReportFactor.reportRequestUrl;
import static com.smile.core.reporter.ReportFactor.reportResponseBody;
import static com.smile.core.reporter.ReportFactor.reportResponseHeaders;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class ApiDriver implements RestAPI {
    private static final String DEFAULT_TIMEOUT = "60";
    private static final String SOCKET_TIMEOUT = "http.socket.reuseaddr";
    private static final String CONNECTION_TIMEOUT = "http.connection.timeout";
    private static final String TOKEN_PATH = "data.token";
    private String bearerToken;
    private final RequestSpecification request;
    private final IAuthentication auth;
    public final Map<String, String> paramMap = new HashMap<>();
    public final Map<String, String> queryMap = new ConcurrentHashMap<>();
    public final Map<String, String> pathMap = new ConcurrentHashMap<>();
    private final int timeout;
    private final boolean isDeepReporting;
    private final String backendUrl;

    public ApiDriver(IAuthentication auth) {
        this.auth = auth;
        request = given().contentType(ContentType.JSON).accept(ContentType.JSON);
        backendUrl = auth.configurator().getBackendUrl();
        timeout = Integer.parseInt(auth.configurator().getParameterOrDefault(ConfigKeys.SOCKET_TIMEOUT, DEFAULT_TIMEOUT));
        isDeepReporting = Boolean.parseBoolean(auth.configurator().getParameterOrDefault(ConfigKeys.DEEP_REPORTING, "false"));
    }

    public ApiResponse get(String url) {
        return get(url, null);
    }

    public ApiResponse get(String url, String payload) {
        url = backendUrl + url;
        RequestSpecification request = generateRequestSpecification(payload);
        reportRequestUrl("GET", url);
        reportRequestHeader(request, isDeepReporting);
        Response response = request.get(url);
        reportResponseHeaders(response, isDeepReporting);
        reportResponseBody(response, isDeepReporting);
        return ApiResponse.of(response);
    }

    @Override
    public ApiResponse post(String url, String payload) {
        url = backendUrl + url;
        RequestSpecification request = generateRequestSpecification(payload);
        reportRequestUrl("POST", url);
        reportRequestHeader(request, isDeepReporting);
        reportRequestBody(payload, isDeepReporting);
        Response response = request.post(url);
        reportResponseHeaders(response, isDeepReporting);
        reportResponseBody(response, isDeepReporting);
        return ApiResponse.of(response);
    }

    public ApiResponse login(String username, String password) {
        ApiResponse response = this.auth.login(username, password);
        if (response.getResponse().statusCode() == HTTP_OK) {
            this.bearerToken = "Bearer " + response.getResponse().jsonPath().getString(TOKEN_PATH);
        }
        return response;
    }

    public ApiResponse login(SmileUsers user) {
        return login(user.getUsername(), user.getPassword());
    }

    public void logout() {
        this.bearerToken = null;
    }

    public ApiResponse delete(String url, String payload) {
        url = backendUrl + url;
        RequestSpecification request = generateRequestSpecification(payload);
        reportRequestUrl("DELETE", url);
        reportRequestHeader(request, isDeepReporting);
        reportRequestBody(payload, isDeepReporting);
        Response response = request.delete(url);
        reportResponseHeaders(response, isDeepReporting);
        reportResponseBody(response, isDeepReporting);
        return ApiResponse.of(response);
    }

    public ApiResponse delete(String url) {
        return delete(url, null);
    }

    public ApiResponse put(String url, String payload) {
        url = backendUrl + url;
        RequestSpecification request = generateRequestSpecification(payload);
        reportRequestUrl("PUT", url);
        reportRequestHeader(request, isDeepReporting);
        reportRequestBody(payload, isDeepReporting);
        Response response = request.put(url);
        reportResponseHeaders(response, isDeepReporting);
        reportResponseBody(response, isDeepReporting);
        return ApiResponse.of(response);
    }

    private RequestSpecification generateRequestSpecification(String payload) {
        RequestSpecification specification = given()
                .config(getConfig())
                .spec(request)
                .header(AUTHORIZATION, this.bearerToken)
                .queryParams(queryMap)
                .pathParams(pathMap)
                .params(paramMap);
        return StringUtils.isEmpty(payload) ? specification : specification.body(payload);
    }

    private RestAssuredConfig getConfig() {
        return RestAssuredConfig.config()
                .httpClient(
                        new HttpClientConfig()
                                .setParam(SOCKET_TIMEOUT, timeout)
                                .setParam(CONNECTION_TIMEOUT, timeout)
                );
    }
}
