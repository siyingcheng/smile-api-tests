package com.smile.apiobjects;


import com.simon.core.apidriver.ApiDriver;
import com.simon.core.errors.SmileApiException;

public class BaseApiObject {
    protected static final String BASE_URL = "/api/v1";
    public ApiDriver apiDriver;

    public BaseApiObject(ApiDriver apiDriver) {
        this.apiDriver = apiDriver;
    }

    public void setPathParameters(String... pairParams) {
        validatePairParameters(pairParams);
        for (int i = 0; i < pairParams.length; i += 2) {
            this.apiDriver.pathMap.put(pairParams[i], pairParams[i + 1]);
        }
    }

    private void validatePairParameters(String[] pairParams) {
        if (pairParams.length % 2 != 0) {
            throw new SmileApiException("Pair parameters must be even");
        }
    }


    public void initParameters() {
        this.apiDriver.pathMap.clear();
        this.apiDriver.queryMap.clear();
        this.apiDriver.paramMap.clear();
    }
}
