package com.smile.core.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.smile.core.config.Configurator;
import com.smile.core.reporter.HtmlMarkup;
import com.smile.core.reporter.Reporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.stream.Collectors;

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
                <p class="font-weight-bold">Groups: %s </p>
                <p class="font-weight-bold">Location: <span class="badge badge-warning">%s</span> </p>
                """.formatted(method.getDescription(), String.join(", ", markupGroups(method.getGroups())), result.getInstanceName());
        if (method.getMethodsDependedUpon().length != 0) {
            description += """
                    <p class="font-weight-bold">Dependencies: %s </p>
                    """.formatted(StringUtils.join(method.getMethodsDependedUpon(), ", "));
        }
        reporter.startTest(method.getMethodName(), description);
        reporter.assignCategory(method.getGroups());
        ITestListener.super.onTestStart(result);
    }

    private CharSequence markupGroups(String[] groups) {
        return Arrays.stream(groups)
                .map(group -> "<span class='badge badge-info'>" + group + "</span>")
                .collect(Collectors.joining(" "));
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
        ITestListener.super.onTestSuccess(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest extentTest = reporter.getTest();
        if (extentTest.getStatus() == Status.PASS) {
            Throwable throwable = result.getThrowable();
            String detailStack = HtmlMarkup.bolder(throwable.getMessage()) + "<br/>" +
                    Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("<br/>"));
            extentTest.log(Status.FAIL, detailStack);
        }
        log.info("onTestFailure");
        ITestListener.super.onTestFailure(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.info("onTestSkipped");
        ITestListener.super.onTestSkipped(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        log.info("onTestFailedWithTimeout");
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
