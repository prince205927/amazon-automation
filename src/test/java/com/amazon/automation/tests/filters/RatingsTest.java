package com.amazon.automation.tests.filters;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class RatingsTest extends BaseTest {
	@Test
	public void verifyRatings() {
		HomePage page = openHomeReady();
		page.searchBar().type("laptop").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		page.filters().clickRatings();
	
		// Assertion 1 (Ratings filter parameters)
		String currentUrl = DriverFactory.getDriver().getCurrentUrl();
		Assert.assertTrue(currentUrl.contains("p_72"), "Brand filter parameters are missing in the URL");
		
		//Assertion 2 (Checking the state of ratings) 
		boolean ratingsState = page.filters().selectedStateCheck();
		Assert.assertTrue(ratingsState, "The ratings should be checked");
		
		//Assertion 3 (verification of filter)
		boolean filterState = results.checkRatings();
		Assert.assertTrue(filterState, "Some products don't have expected ratings");
		
	}
}
