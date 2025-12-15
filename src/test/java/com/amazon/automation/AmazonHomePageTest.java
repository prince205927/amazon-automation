package com.amazon.automation;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.ContinuePage;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.utils.WaitUtils;

public class AmazonHomePageTest {

	@BeforeMethod
	public void setUp() {
		DriverFactory.initDriver();
	}

	@AfterMethod
	public void tearDown() {
		DriverFactory.quitDriver();
	}

	@Test
	public void validateHomePage() {
		HomePage home = new HomePage(DriverFactory.getDriver());
		home.open();
		ContinuePage continued = new ContinuePage(DriverFactory.getDriver());
		if (continued.isDisplayed()) {
			home = continued.continueShopping();
		} else {
			home = new HomePage(DriverFactory.getDriver());
		}
	}
}
