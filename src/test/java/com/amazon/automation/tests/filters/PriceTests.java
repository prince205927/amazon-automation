package com.amazon.automation.tests.filters;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

import static com.amazon.automation.tests.testdata.TestData.*;

@Listeners(com.amazon.automation.tests.listeners.ExtentTestListener.class)

public class PriceTests extends BaseTest {
	@Test(groups = { "filters", "price", "smoke", "regression",
			"p1" }, priority = 1, description = "Verify price filter through custom ranges")
	public void verifyPriceThroughCustomRanges() {

		LoggerUtil.info("===== Test Started: verifyPriceThroughCustomRanges =====");
		ExtentReportLogger.logStep("Verifying price filter using predefined price ranges");

		HomePage page = openHomeReady();
		page.changeLocation(LOCATION_UK);
		page.searchBar().type(SEARCH_LAPTOP).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickPriceRangeByIndex(2);
		Assert.assertTrue(DriverFactory.getDriver().getCurrentUrl().contains("p_36"));

		ExtentReportLogger.pass("Price filter using predefined range applied successfully");

	}

	@Test(groups = { "filters", "price", "regression",
			"p2" }, priority = 2, description = "Verify price filter through slider")
	public void verifyPriceThroughSlider() {

		LoggerUtil.info("===== Test Started: verifyPriceThroughSlider =====");
		ExtentReportLogger.logStep("Verifying price filter using slider control");

		HomePage page = openHomeReady();
		page.changeLocation(LOCATION_UK);
		page.searchBar().type(SEARCH_LAPTOP).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().setPriceRangeBySlider(400, 3000);

		ExtentReportLogger.pass("Price filter using slider applied successfully");

	}
}