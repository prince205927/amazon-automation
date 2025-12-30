package com.amazon.automation.tests.search;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.ContinuePage;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

import static com.amazon.automation.tests.testdata.TestData.*;

@Listeners(com.amazon.automation.tests.listeners.ExtentTestListener.class)

public class SearchKeywordTests extends BaseTest {
	@Test(groups = { "search", "smoke", "regression",
			"p0" }, priority = 1, description = "Verify search with valid keyword")
	public void searchWithValidKeyword() {

		LoggerUtil.info("===== Test Started: searchWithValidKeyword =====");
		ExtentReportLogger.logStep("Searching using a valid keyword");

		HomePage home = openHomeReady();
		home.searchBar().type(SEARCH_LAPTOP);
		home.searchBar().submitSearch();

		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		Assert.assertTrue(results.hasResults(), "At least one search result is needed");

		String term = results.currentSearchTerm();
		Assert.assertTrue(term.toLowerCase().contains(SEARCH_LAPTOP.toLowerCase()),
				"Search heading must reflect the searched keyword");

		ExtentReportLogger.pass("Search with valid keyword verified successfully");

	}

	@Test(groups = { "search", "regression",
			"p1" }, priority = 2, description = "Verify search with mixed case keyword")
	public void searchWithMixedCaseKeyword() {

		LoggerUtil.info("===== Test Started: searchWithMixedCaseKeyword =====");
		ExtentReportLogger.logStep("Searching using a mixed-case keyword");

		HomePage home = openHomeReady();
		home.searchBar().type(RANDOM_TEXT);
		home.searchBar().submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		Assert.assertTrue(results.hasResults(), "At least one search result is needed");

		String term = results.currentSearchTerm();
		Assert.assertTrue(term.toLowerCase().contains(RANDOM_TEXT.toLowerCase()),
				"Search heading must reflect the searched keyword");

		ExtentReportLogger.pass("Search with mixed-case keyword verified successfully");

	}

	@Test(groups = { "search", "regression", "p2" }, priority = 3, description = "Verify search with extra spaces")
	public void searchWithExtraSpaces() {

		LoggerUtil.info("===== Test Started: searchWithExtraSpaces =====");
		ExtentReportLogger.logStep("Searching with keywords containing extra spaces");

		HomePage home = openHomeReady();
		final String expectedKeyword = "bluetooth speaker";
		home.searchBar().type(SPACES_KEYWORD);
		home.searchBar().submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		Assert.assertTrue(results.hasResults(), "At least one search result is needed");

		String term = results.currentSearchTerm();
		Assert.assertTrue(term.toLowerCase().contains(expectedKeyword.toLowerCase()),
				"Extra spaces should be trimmed in search term");

		ExtentReportLogger.pass("Search keyword normalization verified successfully");

	}

	@Test(groups = { "search", "regression",
			"p2" }, priority = 4, description = "Verify search after clearing previous input")
	public void searchAfterClearingPreviousInput() {

		LoggerUtil.info("===== Test Started: searchAfterClearingPreviousInput =====");
		ExtentReportLogger.logStep("Searching after clearing previous input");

		HomePage home = openHomeReady();
		home.searchBar().type(PARTIAL_TEXT);
		home.searchBar().clearSearchBar();
		home.searchBar().type(SEARCH_LAPTOP);
		home.searchBar().submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		String term = results.currentSearchTerm();
		Assert.assertTrue(term.toLowerCase().contains(SEARCH_LAPTOP.toLowerCase()),
				"New search term should be reflected after clearning previous input");
		Assert.assertFalse(term.toLowerCase().contains("iphone"), "old keyword should not appear in results");
		ExtentReportLogger.pass("Search behavior after clearing input verified successfully");
	}
}
