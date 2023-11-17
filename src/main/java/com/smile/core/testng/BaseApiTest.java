package com.smile.core.testng;

import com.smile.models.apidriver.ApiDriver;
import com.smile.models.apidriver.auth.SmileAuthentication;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;

@Slf4j
@Getter
public class BaseApiTest extends BaseTest {
    private ApiDriver apiDriver;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext context) throws Exception {
        super.beforeSuite(context);
        apiDriver = new ApiDriver(new SmileAuthentication(getConfigurator()));
        generateApiDriver();
    }

    private void generateApiDriver() {
        if (apiDriver == null) {
            apiDriver = new ApiDriver(new SmileAuthentication(getConfigurator()));
        }
    }
}