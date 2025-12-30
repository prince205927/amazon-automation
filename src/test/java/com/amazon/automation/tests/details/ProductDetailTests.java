package com.amazon.automation.tests.details;

import static com.amazon.automation.tests.testdata.TestData.*;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.ProductDetailsPage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;
@Listeners(com.amazon.automation.tests.listeners.ExtentTestListener.class)

public class ProductDetailTests extends BaseTest {
	@Test(groups = { "product", "details", "smoke", "regression",
			"p0" }, priority = 1, description = "Verify product details display")
	public void verifyDetails() {
		HomePage home = openHomeReady();
		home.searchBar().type(SEARCH_LAPTOP).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		results.openProductByIndex(1);
		ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
		// Assertion 1 (validating title presence)
		Assert.assertTrue(details.hasTitle(), "Title not present");

		// Assertion 2 (validating ratings)
		Assert.assertTrue(details.hasValidRatings(), "Ratings not present");

		// Assertion3 (validating availability)
		Assert.assertTrue(details.hasAvailabilityText(), "Availability text is not present");

		// Assertion 4 (validating images presence)
		Assert.assertTrue(details.hasImages(), "Images is not present");
	}
}
