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
	public void verifySorting() {
		HomePage page = openHomeReady();
		page.searchBar().type("laptop").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.sorts().selectSort("price-asc-rank");
		results.waitForSpinnerToDisappear();
		// Assertion
		List<Integer> actualPrices = results.getAllPrices();
		List<Integer> sorted = actualPrices.stream().sorted().collect(Collectors.toList());
		Assert.assertEquals(actualPrices, sorted, "Prices are not sorted correctly");
	}
}
