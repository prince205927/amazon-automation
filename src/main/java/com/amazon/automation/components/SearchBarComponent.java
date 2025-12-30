package com.amazon.automation.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.amazon.automation.base.BaseComponent;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;
import com.amazon.automation.utils.WaitUtils;

public class SearchBarComponent extends BaseComponent {

	private final By searchBox = By.id("twotabsearchtextbox");

	private final By submitButton = By.id("nav-search-submit-button");

	private final By suggestions = By.cssSelector("div.autocomplete-results-container div.s-suggestion");

	public SearchBarComponent(WebDriver driver) {
		super(driver);
	}

	public SearchBarComponent type(String text) {

		LoggerUtil.info("Typing into search box: " + text);
		ExtentReportLogger.logStep("Typing search text: " + text);

		WebElement box = wait.visible(searchBox);
		box.clear();
		box.sendKeys(text);
		return this;
	}

	public SearchBarComponent submitSearch() {

		LoggerUtil.info("Submitting search");
        ExtentReportLogger.logStep("Submitting search");

		wait.visible(submitButton).click();

        ExtentReportLogger.pass("Search submitted successfully");

		return this;
	}

	public int getSuggestionsSize() {
		try {
			wait.visibleAll(suggestions);
			return driver.findElements(suggestions).size();
		} catch (NoSuchElementException | TimeoutException e) {
			System.out.println("No suggestions found or timeout occurred: " + e.getMessage());
			return 0;
		}
	}

	public String getSuggestionText(int index) {
		List<WebElement> suggestionList = driver.findElements(suggestions);
		if (suggestionList == null || suggestionList.isEmpty()) {

			LoggerUtil.warn("Suggestion list is empty");

			return "";
		}
		return suggestionList.get(index).getText();
	}

	public boolean clickSuggestionByIndex(int index) {

		 LoggerUtil.info("Clicking suggestion by index: " + index);
		        ExtentReportLogger.logStep("Selecting suggestion at index: " + index);

		try {
			List<WebElement> suggestionList = driver.findElements(suggestions);
			wait.clickable(suggestionList.get(index)).click();

            ExtentReportLogger.pass("Suggestion selected successfully");

			return true;
		} catch (Exception e) {

			LoggerUtil.warn("Failed to click suggestion at index: " + index);
            ExtentReportLogger.logStep("Failed to select suggestion at index: " + index);

			return false;
		}
	}

	public void clearSearchBar() {

		LoggerUtil.info("Clearing search bar");
		ExtentReportLogger.logStep("Clearing search input");

		WebElement box = wait.clickable(searchBox);
		box.click();
		box.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		box.sendKeys(Keys.DELETE);
		wait.invisible(suggestions);

		LoggerUtil.info("Search bar cleared");
		ExtentReportLogger.pass("Search bar cleared");

	}
}
