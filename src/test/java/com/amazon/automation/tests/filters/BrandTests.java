package com.amazon.automation.tests.filters;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;
import static com.amazon.automation.tests.testdata.TestData.*;
@Listeners(com.amazon.automation.tests.listeners.ExtentTestListener.class)

public class BrandTests extends BaseTest {
	@Test(groups = { "filters", "brand", "smoke", "regression",
			"p1" }, priority = 1, description = "Verify brand filter selection")
	public void selectBrand() {
		HomePage page = openHomeReady();
		page.searchBar().type(SEARCH_LAPTOP).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickBrand(BRAND_APPLE);

		// Assertion 1 (Brand filter parameters)
		String currentUrl = DriverFactory.getDriver().getCurrentUrl();
		Assert.assertTrue(currentUrl.contains("p_123"), "Brand filter parameters are missing in the URL");

		// Assertion 2 (State of selection)
		boolean state = page.filters().selectedStateCheck(BRAND_APPLE);
		Assert.assertTrue(state, "The selected brand should be checked");

		// Assertion 3 (verification of filter)
		boolean titleState = results.checkProductTitles(BRAND_APPLE);
		Assert.assertTrue(titleState, "Some title doesn't reflect brand name which means filter is not working");

	}

	@Test(groups = { "filters", "brand", "regression",
			"p1" }, priority = 2, description = "Verify multiple brand filters selection")
	public void selectMultipleBrands() {

		HomePage page = openHomeReady();
		page.searchBar().type(SEARCH_LAPTOP).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickBrand(BRAND_HP);
		page.filters().clickBrand(BRAND_LENOVO);
		Assert.assertTrue(
				page.filters().selectedStateCheck(BRAND_HP) && page.filters().selectedStateCheck(BRAND_LENOVO),
				"The selected brands should be checked");
	}

	@Test(groups = { "filters", "brand", "regression",
			"p2" }, priority = 3, description = "Verify brand filter deselection")
	public void deselectBrandFilter() {

		HomePage page = openHomeReady();
		page.searchBar().type(SEARCH_LAPTOP).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickBrand(BRAND_HP);
		Assert.assertTrue(page.filters().selectedStateCheck(BRAND_HP), "The selected brand should be checked");
		page.filters().clickBrand(BRAND_HP);
		Assert.assertFalse(page.filters().selectedStateCheck(BRAND_HP), "The selected brand should be unchecked");

	}

	@Test(groups = { "filters", "brand", "regression",
			"p2" }, priority = 4, description = "Verify brand filter persists on page refresh")
	public void brandFilterPersistsOnRefresh() {

		HomePage page = openHomeReady();
		page.searchBar().type(SEARCH_LAPTOP).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickBrand(BRAND_MSI);
		Assert.assertTrue(page.filters().selectedStateCheck(BRAND_MSI), "The selected brand should be checked");
		DriverFactory.getDriver().navigate().refresh();
		results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		Assert.assertTrue(page.filters().selectedStateCheck(BRAND_MSI),
				"Even after page refresh, the selected brand checked should persist");
	}

}
