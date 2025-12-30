package com.amazon.automation.tests.pagination;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

import static com.amazon.automation.tests.testdata.TestData.*;

@Listeners(com.amazon.automation.tests.listeners.ExtentTestListener.class)

public class PaginationTests extends BaseTest {
	@Test(groups = { "pagination", "smoke", "regression",
			"p1" }, priority = 1, description = "Verify pagination functionality")
	public void verifyPagination() {

		LoggerUtil.info("===== Test Started: verifyPagination =====");
		ExtentReportLogger.logStep("Verifying pagination functionality in search results");

		HomePage home = openHomeReady();
		home.searchBar().type(SEARCH_LAPTOP).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		results.scrollToPagination();
		results.goToNextPage();
		results.waitForResults();
		// Assertion 1 (validating page number in url)
		Assert.assertTrue(DriverFactory.getDriver().getCurrentUrl().contains("page=2"),
				"Change in page number is not reflected in URL");

		ExtentReportLogger.pass("Pagination successfully navigated to page 2");

	}
}
