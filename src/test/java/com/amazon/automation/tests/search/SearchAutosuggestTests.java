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

	@Test
	public void suggestionsContainPartialText() {
		HomePage home = openHomeReady();
		String partialText = "iphone";
		String firstTwoLetters = partialText.substring(0, 2).toLowerCase();
		home.searchBar().type(partialText);
		int size = home.searchBar().getSuggestionsSize();
		Assert.assertTrue(size > 0, "suggestions should appear for a popular item");

		for (int i = 0; i < size; i++) {
			String suggestion = home.searchBar().getSuggestionText(i).toLowerCase();
			Assert.assertTrue(suggestion.toLowerCase().contains(firstTwoLetters),
					"Suggestion" + suggestion + "must contain" + partialText + "");
		}
	}

	@Test
	public void suggestionsUpdateOnAdditionalTyping() {
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

	}

	@Test
	public void NoSuggestionsForRandomInput() {
		HomePage home = openHomeReady();
		home.searchBar().type("xasfssfawrqwfeg");
		int size = home.searchBar().getSuggestionsSize();
		Assert.assertTrue(size <= 1, "Very few or no suggestions expected for random input");
	}

	@Test
	public void suggestionsDisappearAfterClearing() {
		HomePage home = openHomeReady();
		home.searchBar().type("headphones");
		Assert.assertTrue(home.searchBar().getSuggestionsSize() > 0, "Suggestions should appear");
		home.searchBar().clearSearchBar();
		Assert.assertEquals(home.searchBar().getSuggestionsSize(), 0,
				"suggestions should disappear after clearing the search bar");
	}

	@Test
	public void clickInvalidSuggestionIndex() {
		HomePage home = openHomeReady();
		home.searchBar().type("headphones");
		int validSize = home.searchBar().getSuggestionsSize();
		Assert.assertTrue(validSize > 0, "Suggestions should be present");
		boolean clicked = home.searchBar().clickSuggestionByIndex(validSize + 5);
		System.out.println("\n\n\n" + clicked);
		Assert.assertFalse(clicked, "Clicking invalid index should return false");
	}
}
