package com.amazon.automation.tests.sorts;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.enums.SortOption;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;
import static com.amazon.automation.tests.testdata.TestData.*;
@Listeners(com.amazon.automation.tests.listeners.ExtentTestListener.class)

public class SortTests extends BaseTest {

	@Test(groups = { "sort", "smoke", "regression",
			"p0" }, priority = 1, description = "Verify sort by price low to high")
	public void verifyPriceLowToHighSorting() {
		verifySorting(SortOption.PRICE_LOW_TO_HIGH);
	}

	@Test(groups = { "sort", "smoke", "regression",
			"p1" }, priority = 2, description = "Verify sort by price high to low")
	public void verifyPriceHighToLowSorting() {
		verifySorting(SortOption.PRICE_HIGH_TO_LOW);
	}

	private void verifySorting(SortOption sortOption) {
		HomePage page = openHomeReady();
		page.searchBar().type(SEARCH_LAPTOP).submitSearch();

		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();

		page.sorts().selectSort(sortOption);
		results.waitForSpinnerToDisappear();

		List<Integer> actualPrices = results.getAllPrices();
		List<Integer> expectedPrices = new ArrayList<>(actualPrices);

		if (sortOption.isAscending()) {
			expectedPrices.sort(Integer::compareTo);
		} else {
			expectedPrices.sort((a, b) -> b - a);
		}

		Assert.assertEquals(actualPrices, expectedPrices, "Prices are not sorted correctly");
	}
}
