package com.amazon.automation.tests.search;

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

public class SearchAutosuggestTests extends BaseTest {
	@Test(groups = { "search", "autosuggest", "smoke", "regression",
			"p0" }, priority = 1, description = "Verify visibility of search suggestions")
	public void visibilityOfSuggestions() {

		LoggerUtil.info("===== Test Started: visibilityOfSuggestions =====");

		ExtentReportLogger.logStep("Verifying search autosuggest visibility");

		HomePage home = openHomeReady();
		home.searchBar().type(PARTIAL_TEXT);

		Assert.assertTrue(home.searchBar().getSuggestionsSize() > 0, "Suggestion list cannot be empty");

		String suggestionText = home.searchBar().getSuggestionText(0);

		LoggerUtil.info("First suggestion text: " + suggestionText);
		boolean clicked = home.searchBar().clickSuggestionByIndex(0);
		Assert.assertTrue(clicked, "should be able to click the suggestion");

		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		String headTerm = results.currentSearchTerm();

		LoggerUtil.info("Search results heading: " + headTerm);

		Assert.assertTrue(headTerm.toLowerCase().contains(headTerm), "Heading should reflect clicked suggestion");

		Assert.assertTrue(results.hasResults(), "Expected results after clicking suggestions");

		ExtentReportLogger.pass("Search autosuggest visibility verified successfully");

	}

	@Test(groups = { "search", "autosuggest", "regression",
			"p1" }, priority = 2, description = "Verify suggestions contain partial text")
	public void suggestionsContainPartialText() {

		LoggerUtil.info("===== Test Started: suggestionsContainPartialText =====");
		ExtentReportLogger.logStep("Verifying autosuggest text relevance");

		HomePage home = openHomeReady();
		String firstTwoLetters = PARTIAL_TEXT.substring(0, 2).toLowerCase();
		home.searchBar().type(PARTIAL_TEXT);
		int size = home.searchBar().getSuggestionsSize();
		Assert.assertTrue(size > 0, "suggestions should appear for a popular item");

		for (int i = 0; i < size; i++) {
			String suggestion = home.searchBar().getSuggestionText(i).toLowerCase();
			Assert.assertTrue(suggestion.toLowerCase().contains(firstTwoLetters),
					"Suggestion" + suggestion + "must contain" + PARTIAL_TEXT + "");
		}

		ExtentReportLogger.pass("All suggestions contain partial search text");

	}

	@Test(groups = { "search", "autosuggest", "regression",
			"p1" }, priority = 3, description = "Verify suggestions update on additional typing")
	public void suggestionsUpdateOnAdditionalTyping() {

		LoggerUtil.info("===== Test Started: suggestionsUpdateOnAdditionalTyping =====");
		ExtentReportLogger.logStep("Verifying autosuggest updates on additional typing");

		HomePage home = openHomeReady();
		home.searchBar().type("sams");
		int initialSize = home.searchBar().getSuggestionsSize();
		Assert.assertTrue(initialSize > 0, "initial suggestions should appear");
		home.searchBar().type("u");
		int updatedSize = home.searchBar().getSuggestionsSize();
		Assert.assertTrue(updatedSize > 0 && updatedSize <= initialSize,
				"Suggestions should refine after typing more characters");
		String fullText = "samsu";
		for (int i = 0; i < updatedSize; i++) {
			String suggestion = home.searchBar().getSuggestionText(i);
			Assert.assertTrue(suggestion.toLowerCase().contains(fullText.toLowerCase()),
					"Suggestion" + suggestion + "must contain" + fullText + "");
		}

		ExtentReportLogger.pass("Autosuggest refinement verified successfully");

	}

	@Test(groups = { "search", "autosuggest", "negative", "regression",
			"p2" }, priority = 4, description = "Verify no suggestions for random input")
	public void NoSuggestionsForRandomInput() {

		LoggerUtil.info("===== Test Started: NoSuggestionsForRandomInput =====");
		ExtentReportLogger.logStep("Verifying autosuggest behavior for random input");

		HomePage home = openHomeReady();
		home.searchBar().type(RANDOM_TEXT);
		int size = home.searchBar().getSuggestionsSize();

		LoggerUtil.info("Suggestion count for random input: " + size);

		Assert.assertTrue(size <= 1, "Very few or no suggestions expected for random input");

		ExtentReportLogger.pass("Autosuggest correctly handled random input");

	}

	@Test(groups = { "search", "autosuggest", "regression",
			"p2" }, priority = 5, description = "Verify suggestions disappear after clearing")
	public void suggestionsDisappearAfterClearing() {

		LoggerUtil.info("===== Test Started: suggestionsDisappearAfterClearing =====");
		ExtentReportLogger.logStep("Verifying autosuggest disappears after clearing input");

		HomePage home = openHomeReady();
		home.searchBar().type(SEARCH_LAPTOP);
		Assert.assertTrue(home.searchBar().getSuggestionsSize() > 0, "Suggestions should appear");
		home.searchBar().clearSearchBar();
		Assert.assertEquals(home.searchBar().getSuggestionsSize(), 0,
				"suggestions should disappear after clearing the search bar");

		ExtentReportLogger.pass("Autosuggest cleared successfully");

	}

	@Test(groups = { "search", "autosuggest", "negative", "regression",
			"p2" }, priority = 6, description = "Verify click on invalid suggestion index")
	public void clickInvalidSuggestionIndex() {

		LoggerUtil.info("===== Test Started: clickInvalidSuggestionIndex =====");
		ExtentReportLogger.logStep("Verifying invalid autosuggest click handling");

		HomePage home = openHomeReady();
		home.searchBar().type(SEARCH_LAPTOP);
		int validSize = home.searchBar().getSuggestionsSize();
		Assert.assertTrue(validSize > 0, "Suggestions should be present");
		boolean clicked = home.searchBar().clickSuggestionByIndex(validSize + 5);
		System.out.println("\n\n\n" + clicked);
		Assert.assertFalse(clicked, "Clicking invalid index should return false");

		ExtentReportLogger.pass("Invalid autosuggest index handled correctly");

	}
}
