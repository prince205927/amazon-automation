package com.amazon.automation.tests.search;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.ContinuePage;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class SearchKeywordTests extends BaseTest {
	@Test(description = "TC-01: Verify relevant products are displayed for valid keywords")
	public void searchWithValidKeyword() {
		HomePage home = openHomeReady();
		final String keyword = "laptop";
		home.searchBar().type(keyword);
		home.searchBar().submitSearch();
		
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		Assert.assertTrue(results.hasResults(), "At least one search result is needed");
		
		String term = results.currentSearchTerm();
		Assert.assertTrue(term.toLowerCase().contains(keyword.toLowerCase()), "Search heading must reflect the searched keyword");
		
	}
}
