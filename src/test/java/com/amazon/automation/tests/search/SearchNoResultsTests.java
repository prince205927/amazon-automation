package com.amazon.automation.tests.search;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import static com.amazon.automation.tests.testdata.TestData.*;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

@Listeners(com.amazon.automation.tests.listeners.ExtentTestListener.class)

public class SearchNoResultsTests extends BaseTest {
	@Test(groups = { "search", "negative", "regression",
			"p1" }, priority = 1, description = "Verify no results message for invalid keywords")
	public void searchWithInvalidKeywords() {

		LoggerUtil.info("===== Test Started: searchWithInvalidKeywords =====");
		ExtentReportLogger.logStep("Searching with invalid keyword to verify no results scenario");

		HomePage home = openHomeReady();
		home.searchBar().type(RANDOM_TEXT);
		home.searchBar().submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		Assert.assertFalse(results.hasResults(), "No result tiles should be there for invalid keyword");

		ExtentReportLogger.pass("No search results correctly displayed for invalid keyword");

	}
}
