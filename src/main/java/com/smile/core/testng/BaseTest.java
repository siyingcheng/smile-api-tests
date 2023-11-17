package com.smile.core.testng;

import com.smile.core.asserts.SoftAssertions;
import com.smile.core.config.Configurator;
import com.smile.core.listeners.SmileTestListener;
import com.smile.core.reporter.Reporter;
import com.smile.utils.FileUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

@Getter
@Slf4j
@Listeners(SmileTestListener.class)
public abstract class BaseTest {
    private Configurator configurator;
    protected SoftAssertions assertions = new SoftAssertions();
    protected Reporter reporter = Reporter.getInstance();

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext context) throws Exception {
        configurator = Configurator.getInstance();
        configurator.initParameters(context.getCurrentXmlTest().getAllParameters());
        // initialize reporter and logs locations
        FileUtils.createDirectory(configurator.getReportPath());
        FileUtils.createDirectory(configurator.getLogPath());
    }
}
