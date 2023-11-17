package com.smile.core.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.smile.core.config.Configurator;
import com.smile.core.reporter.Reporter;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

@Slf4j
public class SmileTestListener implements ITestListener {
    private final Reporter reporter = Reporter.getInstance();
    private final Configurator configurator = Configurator.getInstance();

    @Override
    public void onTestStart(ITestResult result) {
        log.info("onTestStart");
        ITestNGMethod method = result.getMethod();
        String description = """
                <p class="font-weight-bold">Description: <span class="badge badge-light">%s</span> </p>
                <p class="font-weight-bold">Groups: <span class="badge badge-info">%s</span> </p>
                <p class="font-weight-bold">Location: <span class="badge badge-warning">%s</span> </p>
                """.formatted(method.getDescription(), String.join(", ", method.getGroups()), result.getInstanceName());
        reporter.createTest(method.getMethodName(), description);
        ExtentTest extentTest = reporter.getTest();
        extentTest.assignCategory(method.getGroups());
        ITestListener.super.onTestStart(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Because used soft assertion, case will not throw any AssertionError, so always in here
        Status status = reporter.getTest().getStatus();
        if (status == Status.FAIL) {
            result.setStatus(ITestResult.FAILURE);
            onTestFailure(result);
            return;
        }
        log.info("onTestSuccess");
        doOnOneTestComplete();
        ITestListener.super.onTestSuccess(result);
    }

    private void doOnOneTestComplete() {
        reporter.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.info("onTestFailure");
        doOnOneTestComplete();
        ITestListener.super.onTestFailure(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.info("onTestSkipped");
        doOnOneTestComplete();
        ITestListener.super.onTestSkipped(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        log.info("onTestFailedWithTimeout");
        doOnOneTestComplete();
        ITestListener.super.onTestFailedWithTimeout(result);
    }

    @Override
    public void onStart(ITestContext context) {
        log.info("onStart");
        reporter.initReporter(configurator.getReportPath());
        ITestListener.super.onStart(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("onFinish");
        reporter.endReport();
        ITestListener.super.onFinish(context);
    }
}