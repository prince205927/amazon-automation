package com.amazon.automation.tests.pagination;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class PaginationTests extends BaseTest {
	@Test
	public void verifyPagination() {
		HomePage home = openHomeReady();
		home.searchBar().type("laptop").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		results.scrolltoPagination();
		results.goToNextPage();
		results.waitForResults();
		try {
			Thread.sleep(5000);
		}
		catch(Exception e){
			
		}
		
		//Assertion 1 (validating page number in url)
		Assert.assertTrue(DriverFactory.getDriver().getCurrentUrl().contains("page=2"), "Change in page number is not reflected in URL");
		
		
	}
}
