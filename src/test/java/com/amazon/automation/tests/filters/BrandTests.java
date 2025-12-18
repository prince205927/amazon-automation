package com.amazon.automation.tests.filters;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class BrandTests extends BaseTest {
	@Test
	public void selectBrand() {
		String brand = "Apple";
		HomePage page = openHomeReady();
		page.searchBar().type("laptop").submitSearch();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		page.filters().clickBrand(brand);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Assertion 1 (Brand filter parameters)
		String currentUrl = DriverFactory.getDriver().getCurrentUrl();
		Assert.assertTrue(currentUrl.contains("p_123"), "Brand filter parameters are missing in the URL");

		// Assertion 2 (State of selection)
		boolean state = page.filters().selectedStateCheck(brand);
		Assert.assertTrue(state, "The selected brand should be checked");

		// Assertion 3 (verification of filter)
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		boolean titleState = results.checkProductTitles(brand);
		Assert.assertTrue(titleState, "Some title doesn't reflect brand name which means filter is not working");

	}
}
