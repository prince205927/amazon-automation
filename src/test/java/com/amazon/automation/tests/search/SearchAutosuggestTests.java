package com.amazon.automation.tests.search;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class SearchAutosuggestTests extends BaseTest {
	@Test(description = "TC-03: Verify search suggestions appear and are clickable")
	public void visibilityOfSuggestions() {
		HomePage home = openHomeReady();
		String partialText = "lap";
		home.searchBar().type(partialText);

		Assert.assertTrue(home.searchBar().getSuggestionsSize() > 0, "Suggestion list cannot be empty");

		String suggestionText = home.searchBar().getSuggestionText(2);
		System.out.println(suggestionText);
		boolean clicked = home.searchBar().clickSuggestionByIndex(2);
		Assert.assertTrue(clicked, "should be able to click the suggestion");

		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		String headTerm = results.currentSearchTerm();
		Assert.assertTrue(headTerm.toLowerCase().contains(headTerm), "Heading should reflect clicked suggestion");

		Assert.assertTrue(results.hasResults(), "Expected results after clicking suggestions");
	}
}
