package com.smile.core.api;

import io.restassured.response.Response;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
}
