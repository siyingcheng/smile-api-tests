package com.smile.core.apidriver;

import com.smile.core.api.ApiResponse;
import com.smile.core.config.ConfigKeys;
import com.smile.core.reporter.Reporter;
import com.smile.core.apidriver.auth.IAuthentication;
import com.smile.apiobjects.user.SmileUsers;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.smile.core.reporter.ReportFactor.reportRequestHeader;
import static com.smile.core.reporter.ReportFactor.reportRequestUrl;
import static io.restassured.RestAssured.given;

@Slf4j
public class ApiDriver implements RestAPI {
    private static final String DEFAULT_TIMEOUT = "60";
    private static final String SOCKET_TIMEOUT = "http.socket.reuseaddr";
    private static final String CONNECTION_TIMEOUT = "http.connection.timeout";
    protected final Reporter reporter = Reporter.getInstance();
    private String bearerToken;
    private final RequestSpecification request;
    private final IAuthentication auth;
    private final Map<String, String> paramMap = new HashMap<>();
    private final Map<String, String> queryMap = new ConcurrentHashMap<>();
    private final Map<String, String> pathMap = new ConcurrentHashMap<>();
    private final int timeout;
    private final boolean isDeepReporting;
    private String backendUrl;

    public ApiDriver(IAuthentication auth) {
        this.auth = auth;
        request = given().contentType(ContentType.JSON).accept(ContentType.JSON);
        backendUrl = auth.configurator().getBackendUrl();
        timeout = Integer.parseInt(auth.configurator().getParameterOrDefault(ConfigKeys.SOCKET_TIMEOUT, DEFAULT_TIMEOUT));
        isDeepReporting = Boolean.parseBoolean(auth.configurator().getParameterOrDefault(ConfigKeys.DEEP_REPORTING, "false"));
    }

    @Override
    public ApiResponse post(String url, String payload) {
        url = backendUrl + url;
        RequestSpecification request = generateRequestSpecification(payload);
        reportRequestUrl("POST", url);
        reportRequestHeader(request, isDeepReporting);
        return null;
    }

    public ApiResponse login(String username, String password) {
        return this.auth.login(username, password);
    }

    public ApiResponse login(SmileUsers user) {
        return login(user.getUsername(), user.getPassword());
    }

    private RequestSpecification generateRequestSpecification(String payload) {
        RequestSpecification specification = given()
                .config(getConfig())
                .spec(request)
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
