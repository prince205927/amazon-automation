package com.amazon.automation;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;

public class SimpleTest {
	@BeforeMethod
	public void setUp() {
		DriverFactory.initDriver();
	}
	
	@AfterMethod
	public void tearDown() {
		DriverFactory.quitDriver();
	}
	
	@Test
	public void openWebsite() {
		WebDriver driver = DriverFactory.getDriver();
		driver.get("https://example.com");
	}
}
