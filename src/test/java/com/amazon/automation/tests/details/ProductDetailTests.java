package com.amazon.automation.tests.details;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.ProductDetailsPage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class ProductDetailTests extends BaseTest {
	@Test
	public void verifyDetails() {
		HomePage home = openHomeReady();
		home.searchBar().type("laptop").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		results.openProductByIndex(1);
		ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
		//Assertion 1 (validating title presence)
		Assert.assertTrue(details.hasTitle(), "Title not present");

		//Assertion 2 (validating ratings)
		Assert.assertTrue(details.hasValidRatings(), "Ratings not present");

		//Assertion3 (validating availability)
		Assert.assertTrue(details.hasAvailabilityText(), "Availability text is not present");
		
		//Assertion 4 (validating images presence)
		Assert.assertTrue(details.hasImages(), "Images is not present");
	}
}
