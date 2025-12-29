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
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickBrand(brand);

		// Assertion 1 (Brand filter parameters)
		String currentUrl = DriverFactory.getDriver().getCurrentUrl();
		Assert.assertTrue(currentUrl.contains("p_123"), "Brand filter parameters are missing in the URL");

		// Assertion 2 (State of selection)
		boolean state = page.filters().selectedStateCheck(brand);
		Assert.assertTrue(state, "The selected brand should be checked");

		// Assertion 3 (verification of filter)
		boolean titleState = results.checkProductTitles(brand);
		Assert.assertTrue(titleState, "Some title doesn't reflect brand name which means filter is not working");

	}
	
	@Test
	public void selectMultipleBrands() {
		String searchKeyword = "laptop";
		String brand1 = "HP";
		String brand2 = "Lenovo";
		HomePage page = openHomeReady();
		page.searchBar().type(searchKeyword).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickBrand(brand1);
		page.filters().clickBrand(brand2);
		Assert.assertTrue(page.filters().selectedStateCheck(brand1)&&page.filters().selectedStateCheck(brand2), "The selected brands should be checked");
	}
	
	@Test
	public void deselectBrandFilter() {
		String searchKeyword = "laptop";
		String brand = "HP";
		HomePage page = openHomeReady();
		page.searchBar().type(searchKeyword).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickBrand(brand);
		Assert.assertTrue(page.filters().selectedStateCheck(brand), "The selected brand should be checked");
		page.filters().clickBrand(brand);
		Assert.assertFalse(page.filters().selectedStateCheck(brand), "The selected brand should be unchecked");
		
	}
	
	@Test
	public void brandFilterPersistsOnRefresh() {
		String searchKeyword = "laptop";
		String brand = "msi";
		HomePage page = openHomeReady();
		page.searchBar().type(searchKeyword).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickBrand(brand);
		Assert.assertTrue(page.filters().selectedStateCheck(brand), "The selected brand should be checked");
		DriverFactory.getDriver().navigate().refresh();
		results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		Assert.assertTrue(page.filters().selectedStateCheck(brand), "Even after page refresh, the selected brand checked should persist");
	}
	
	
}
