package com.amazon.automation.tests.search;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class SearchNoResultsTests extends BaseTest {
	@Test(description = "TC-02: Verify 'No results' state for invalid keywords")
	public void searchWithInvalidKeywords() {
		HomePage home = openHomeReady();
		final String invalidKeyword = "safserfwR211421323241121212";
		home.searchBar().type(invalidKeyword);
		home.searchBar().submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		Assert.assertFalse(results.hasResults(), "No result tiles should be there for invalid keyword");
	}
}
