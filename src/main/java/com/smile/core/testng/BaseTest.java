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

import java.util.Random;

@Getter
@Slf4j
@Listeners(SmileTestListener.class)
public abstract class BaseTest {
    protected Configurator configurator = Configurator.getInstance();
    protected SoftAssertions assertion = new SoftAssertions();
    protected Reporter reporter = Reporter.getInstance();

    /**
     * Each test class should use this method in @BeforeClass
     */
    public void initTest() {
        reporter.removeTest();
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite(ITestContext context) throws Exception {
        configurator.initParameters(context.getCurrentXmlTest().getAllParameters());
        // initialize reporter and logs locations
        FileUtils.createDirectory(configurator.getReportPath());
        FileUtils.createDirectory(configurator.getLogPath());
    }

    public String getUniqueNumberStr(int decimalPlaces) {
        String format = String.format("%%0%sd", decimalPlaces);
        return String.format(format, new Random().nextInt((int) Math.pow(10, decimalPlaces)));
    }
}
