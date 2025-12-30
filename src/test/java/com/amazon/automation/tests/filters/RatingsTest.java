package com.amazon.automation.tests.filters;

import static com.amazon.automation.tests.testdata.TestData.*;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

@Listeners(com.amazon.automation.tests.listeners.ExtentTestListener.class)

public class RatingsTest extends BaseTest {
	@Test(groups = { "filters", "ratings", "smoke", "regression",
			"p1" }, priority = 1, description = "Verify ratings filter")
	public void verifyRatings() {

		LoggerUtil.info("===== Test Started: verifyRatings =====");
		ExtentReportLogger.logStep("Verifying ratings filter functionality");

		HomePage page = openHomeReady();
		page.searchBar().type(SEARCH_LAPTOP).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickRatings();

		// Assertion 1 (Ratings filter parameters)
		String currentUrl = DriverFactory.getDriver().getCurrentUrl();
		Assert.assertTrue(currentUrl.contains("p_72"), "Brand filter parameters are missing in the URL");

		// Assertion 2 (Checking the state of ratings)
		boolean ratingsState = page.filters().selectedStateCheck();
		Assert.assertTrue(ratingsState, "The ratings should be checked");

		// Assertion 3 (verification of filter)
		boolean filterState = results.checkRatings();
		Assert.assertTrue(filterState, "Some products don't have expected ratings");

		ExtentReportLogger.pass("Ratings filter applied and validated successfully");

	}
}
