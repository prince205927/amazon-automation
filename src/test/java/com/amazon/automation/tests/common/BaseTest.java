package com.amazon.automation.tests.common;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.ContinuePage;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.tests.listeners.ExtentTestListener;
import com.amazon.automation.tests.utils.NavigationHelper;
import com.amazon.automation.utils.DatabaseUtil;
import com.amazon.automation.utils.ExtentManager;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

@Listeners(ExtentTestListener.class)

public class BaseTest {
    protected Logger logger;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        LoggerUtil.info("========================================");
        LoggerUtil.info("TEST SUITE STARTED");
        LoggerUtil.info("========================================");
        DatabaseUtil.initializeDatabase();
        DatabaseUtil.insertOrUpdateWebsite("amazon", "https://www.amazon.com/");
        ExtentReportLogger.info("Test Suite Execution Started");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        LoggerUtil.info("WebDriver intiialized for test:"+ this.getClass().getSimpleName());
        ExtentReportLogger.info("Browser launched and maximized");
        LoggerUtil.info("Navigating to Amazon homepage");
        ExtentReportLogger.logStep("Navigating to amazon.com");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        LoggerUtil.info("Tearing down test: " + this.getClass().getSimpleName());
        DriverFactory.quitDriver();
        ExtentManager.removeTest();
        LoggerUtil.removeLogger();
        LoggerUtil.info("WebDriver closed successfully");
        LoggerUtil.info("Browser closed");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        DatabaseUtil.closeConnection();
        LoggerUtil.info("========================================");
        LoggerUtil.info("TEST SUITE COMPLETED");
        LoggerUtil.info("========================================");
        ExtentReportLogger.info("Test Suite Execution Completed");
    }

    protected HomePage openHomeReady() {
        LoggerUtil.info("Opening Amazon homepage");
        ExtentReportLogger.logStep("Opening Amazon homepage");
        NavigationHelper navigationHelper = new NavigationHelper(DriverFactory.getDriver());
        navigationHelper.navigateToWebsite("amazon");
        ExtentReportLogger.info("Navigated to https://www.amazon.com");
        DriverFactory.getDriver().manage().deleteAllCookies();
        LoggerUtil.info("All cookies cleared");
        ExtentReportLogger.info("Cookies cleared");
        ContinuePage continued = new ContinuePage(DriverFactory.getDriver());
        
        if (continued.isDisplayed()) {
            LoggerUtil.info("Continue page displayed, clicking continue shopping");
            ExtentReportLogger.info("Continue page displayed, clicking continue shopping");
            return continued.continueShopping();
        } else {
            LoggerUtil.info("Homepage loaded directly");
            ExtentReportLogger.info("Homepage loaded successfully");
            return new HomePage(DriverFactory.getDriver());
        }
    }
}