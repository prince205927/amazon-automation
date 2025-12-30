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

public class VariantTests extends BaseTest {
	@Test(groups = { "product", "variants", "smoke", "regression",
			"p0" }, priority = 1, description = "Verify color variant selection")
	public void verifyColor() {
		HomePage home = openHomeReady();
		home.changeLocation(LOCATION_UK);
		home.searchBar().type("shirt").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		results.openProductByIndex(1);
		ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
		String choosenColor = details.goToVariations().chooseColor(SEARCH_COLOR);
		Assert.assertEquals(SEARCH_COLOR, choosenColor, "The searched color is not chosen");
		String choosenSize = details.goToVariations().chooseSize(SEARCH_SIZE);
		Assert.assertEquals(SEARCH_SIZE, choosenSize, "The searched size is not chosen");
		Double fullPrice = details.goToVariations().getPrice();
		System.out.println("\n\n\nThe full price is " + fullPrice);
		Assert.assertFalse(details.goToVariations().isBlank(), "Price cannot be blank");
		String availabilityStatus = details.goToVariations().getAvailabilityStatus();
		System.out.println("\n\n\nThe availability status is " + availabilityStatus);
		Assert.assertFalse(details.goToVariations().isAvailabilityStatusBlank(), "Availability Status can't be blank");

	}
}
