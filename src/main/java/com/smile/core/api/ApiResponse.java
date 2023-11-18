package com.smile.core.api;

import io.restassured.response.Response;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ApiResponse {
    private Response response;

    public static ApiResponse of(Response response) {
        return new ApiResponse().setResponse(response);
    }

    public int statusCode() {
        return response.statusCode();
    }

    public <T> T getObjectFromJsonPath(String jsonPath, Class<T> genericType) {
        return response.getBody().jsonPath().getObject(jsonPath, genericType);
    }

    public String getStringFromJsonPath(String jsonPath) {
        return response.getBody().jsonPath().getString(jsonPath);
    }

    public List<?> getListFromJsonPath(String jsonPath) {
        return response.getBody().jsonPath().getList(jsonPath);
    }

    public List<?> getListFromJsonPath(String jsonPath, Class<?> genericType) {
        return response.getBody().jsonPath().getList(jsonPath, genericType);
    }
}
