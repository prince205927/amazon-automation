package com.amazon.automation.tests.listeners;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.aventstack.extentreports.markuputils.ExtentColor;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.utils.ExtentManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class ExtentTestListener implements ITestListener {
    private static ExtentReports extent;

    @Override
    public void onStart(ITestContext context) {
        extent = ExtentManager.createInstance();
        System.out.println("========== Test Suite Started ==========");
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(
            result.getTestClass().getName() + " :: " + result.getMethod().getMethodName()
        );
        ExtentManager.setTest(test);
        
        ExtentManager.getTest().log(Status.INFO, 
            MarkupHelper.createLabel("Test Started: " + result.getMethod().getMethodName(), ExtentColor.BLUE));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().log(Status.PASS, 
            MarkupHelper.createLabel("Test Passed: " + result.getMethod().getMethodName(), ExtentColor.GREEN));
        
        ExtentManager.getTest().pass("Test Duration: " + getExecutionTime(result) + " seconds");
        ExtentManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentManager.getTest().log(Status.FAIL, 
            MarkupHelper.createLabel("Test Failed: " + result.getMethod().getMethodName(), ExtentColor.RED));
        
        ExtentManager.getTest().fail(result.getThrowable());
        
        // Capture screenshot on failure
        String screenshotPath = captureScreenshot(result.getMethod().getMethodName());
        if (screenshotPath != null) {
            try {
                ExtentManager.getTest().addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
            } catch (Exception e) {
                ExtentManager.getTest().fail("Failed to attach screenshot: " + e.getMessage());
            }
        }
        
        ExtentManager.getTest().fail("Test Duration: " + getExecutionTime(result) + " seconds");
        ExtentManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentManager.getTest().log(Status.SKIP, 
            MarkupHelper.createLabel("Test Skipped: " + result.getMethod().getMethodName(), ExtentColor.YELLOW));
        
        ExtentManager.getTest().skip(result.getThrowable());
        ExtentManager.removeTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
        System.out.println("========== Test Suite Finished ==========");
        System.out.println("Report Location: test-output/ExtentReport.html");
    }

    private String captureScreenshot(String testName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                return null;
            }
            
            TakesScreenshot ts = (TakesScreenshot) driver;
            byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
            
            // Create screenshots directory
            String screenshotDir = "test-output/screenshots/";
            File directory = new File(screenshotDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Generate unique filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";
            Path screenshotPath = Paths.get(screenshotDir + fileName);
            
            // Save screenshot
            Files.write(screenshotPath, screenshot);
            
            return screenshotPath.toString();
        } catch (Exception e) {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    private String getExecutionTime(ITestResult result) {
        long duration = (result.getEndMillis() - result.getStartMillis()) / 1000;
        return String.valueOf(duration);
    }
}
