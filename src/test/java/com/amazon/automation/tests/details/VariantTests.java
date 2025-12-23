package com.amazon.automation.tests.details;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.ProductDetailsPage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class VariantTests extends BaseTest {
	@Test
	public void verifyColor() {
		HomePage home = openHomeReady();
		home.changeLocation("United Kingdom");
		String searchedColor = "Charcoal Heather";
		String searchedSize = "Medium";
		home.searchBar().type("shirt").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		results.openProductByIndex(1);
		ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
		String choosenColor = details.goToVariations().chooseColor(searchedColor);
		Assert.assertEquals(searchedColor, choosenColor, "The searched color is not chosen");
		String choosenSize= details.goToVariations().chooseSize(searchedSize);
		Assert.assertEquals(searchedSize, choosenSize, "The searched size is not chosen");
		Double fullPrice = details.goToVariations().getPrice();
		System.out.println("\n\n\nThe full price is "+ fullPrice);
		Assert.assertFalse(details.goToVariations().isBlank(), "Price cannot be blank");
		String availabilityStatus = details.goToVariations().getAvailabilityStatus();
		System.out.println("\n\n\nThe availability status is "+ availabilityStatus);
		Assert.assertFalse(details.goToVariations().isAvailabilityStatusBlank(), "Availability Status can't be blank");

	}
}
