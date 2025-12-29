package com.amazon.automation.tests.common;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.ContinuePage;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.utils.LoggerUtil;

public class BaseTest {
    protected Logger logger;

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        LoggerUtil.info("========================================");
        LoggerUtil.info("TEST SUITE STARTED");
        LoggerUtil.info("========================================");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        logger = LoggerUtil.getLogger(this.getClass());
        logger.info("Setting up test: " + this.getClass().getSimpleName());
        DriverFactory.initDriver();
        logger.info("WebDriver initialized successfully");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("Tearing down test: " + this.getClass().getSimpleName());
        DriverFactory.quitDriver();
        logger.info("WebDriver closed successfully");
        LoggerUtil.removeLogger();
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        LoggerUtil.info("========================================");
        LoggerUtil.info("TEST SUITE COMPLETED");
        LoggerUtil.info("========================================");
    }

    protected HomePage openHomeReady() {
        logger.info("Opening Amazon homepage");
        DriverFactory.getDriver().get("https://www.amazon.com/");
        ContinuePage continued = new ContinuePage(DriverFactory.getDriver());
        DriverFactory.getDriver().manage().deleteAllCookies();
        logger.info("Cookies cleared");
        
        if (continued.isDisplayed()) {
            logger.info("Continue page displayed, clicking continue shopping");
            return continued.continueShopping();
        } else {
            logger.info("Homepage loaded directly");
            return new HomePage(DriverFactory.getDriver());
        }
    }
}