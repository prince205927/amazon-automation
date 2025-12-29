package com.amazon.automation.tests.filters;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class PriceTests extends BaseTest {
	@Test
	public void verifyPriceThroughCustomRanges() {
		HomePage page = openHomeReady();
		page.changeLocation("United Kingdom");
		page.searchBar().type("laptop").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickPriceRange("Up to $400");
		Assert.assertTrue(DriverFactory.getDriver().getCurrentUrl().contains("p_36"));
}
	@Test
	public void verifyPriceThroughSlider() {
		HomePage page = openHomeReady();
		page.changeLocation("United Kingdom");
		page.searchBar().type("laptop").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
	    page.filters().setPriceRangeBySlider(400, 3000);
	}
}