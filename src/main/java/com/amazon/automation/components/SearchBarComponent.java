package com.amazon.automation.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.amazon.automation.base.BaseComponent;
import com.amazon.automation.utils.WaitUtils;

public class SearchBarComponent extends BaseComponent {

	private final By searchBox = By.id("twotabsearchtextbox");

	private final By submitButton = By.id("nav-search-submit-button");

	private final By suggestions = By.cssSelector("div.autocomplete-results-container div.s-suggestion");

	public SearchBarComponent(WebDriver driver) {
		super(driver);
	}

	public SearchBarComponent type(String text) {
		WebElement box = wait.visible(searchBox);
		box.clear();
		box.sendKeys(text);
		return this;
	}

	public SearchBarComponent submitSearch() {
		wait.visible(submitButton).click();
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
		if (suggestionList == null || suggestionList.isEmpty())
			return "";
		return suggestionList.get(index).getText();
	}

	public boolean clickSuggestionByIndex(int index) {
		List<WebElement> suggestionList = driver.findElements(suggestions);
		if (suggestionList == null || suggestionList.isEmpty())
			return false;
		try {
			wait.clickable(suggestionList.get(index)).click();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void clearSearchBar() {
		WebElement box = wait.clickable(searchBox);
		box.click();
		box.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		box.sendKeys(Keys.DELETE);
		wait.invisible(suggestions);
	}
}
