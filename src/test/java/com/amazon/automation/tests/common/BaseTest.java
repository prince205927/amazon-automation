package com.amazon.automation.tests.common;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.ContinuePage;
import com.amazon.automation.pages.HomePage;

public class BaseTest {
	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		DriverFactory.initDriver();
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		DriverFactory.quitDriver();
	}

	protected HomePage openHomeReady() {
		DriverFactory.getDriver().get("https://www.amazon.com/");
		ContinuePage continued = new ContinuePage(DriverFactory.getDriver());
		DriverFactory.getDriver().manage().deleteAllCookies();
		return continued.isDisplayed() ? continued.continueShopping() : new HomePage(DriverFactory.getDriver());
	}
}
