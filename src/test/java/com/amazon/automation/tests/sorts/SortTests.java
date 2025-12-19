package com.amazon.automation.tests.sorts;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class SortTests extends BaseTest {

	@Test
	public void verifyPriceLowToHighSorting() {
		verifySorting("price-asc-rank", true);
	}

	@Test
	public void verifyPriceHighToLowSorting() {
		verifySorting("price-desc-rank", false);
	}

	private void verifySorting(String sortValue, boolean ascending) {
		HomePage page = openHomeReady();
		page.searchBar().type("laptop").submitSearch();

		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();

		page.sorts().selectSort(sortValue);
		results.waitForSpinnerToDisappear();

		List<Integer> actualPrices = results.getAllPrices();
		List<Integer> expectedPrices = new ArrayList<>(actualPrices);

		if (ascending) {
			expectedPrices.sort(Integer::compareTo);
		} else {
			expectedPrices.sort((a, b) -> b - a);
		}

		Assert.assertEquals(actualPrices, expectedPrices, "Prices are not sorted correctly");
	}
}
