package com.smile.core.reporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.smile.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.testng.IReporter;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.smile.core.reporter.HtmlMarkup.bolder;

@Slf4j
public class Reporter implements IReporter {
    private static final SimpleDateFormat REPORT_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private ExtentReports extent;
    private static Reporter instance;
    private final Map<Long, ExtentTest> extentTestMap = new ConcurrentHashMap<>();

    private Reporter() {
    }

    public static synchronized Reporter getInstance() {
        if (instance == null) {
            instance = new Reporter();
        }
        return instance;
    }

    public void log(Status status, String details) {
        getTest().log(status, details);
    }

    public void logStep(String stepDetails) {
        log.info(stepDetails);
        getTest().info(bolder(stepDetails));
    }


    public ExtentTest getTest() {
        return extentTestMap.get(Thread.currentThread().threadId());
    }

    public synchronized void createTest(String name, String description) {
        extentTestMap.put(Thread.currentThread().threadId(), extent.createTest(name, description));
    }

    public synchronized void createTest(String name) {
        createTest(name, null);
    }

    public synchronized void removeTest() {
        extentTestMap.remove(Thread.currentThread().threadId());
    }

    public void initReporter(Path reportPath) {
        String timestamp = REPORT_FORMAT.format(Calendar.getInstance().getTime());
        Path directory = reportPath.resolve(timestamp);
        FileUtils.createDirectory(directory);
        Path reportFile = directory.resolve(String.format("Report_%s.html", timestamp));
        extent = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter(reportFile.toString());
        extent.attachReporter(spark);
        spark.config().setTheme(Theme.DARK);
        log.info("Extent-reports generated: {}", reportFile);
    }

    public void endReport() {
        extent.flush();
    }
}
