package com.amazon.automation.components;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.amazon.automation.utils.WaitUtils;

public class SearchBarComponent {
	private final WebDriver driver;
	private final WaitUtils wait;

	@FindBy(id = "twotabsearchtextbox")
	private WebElement searchBox;

	@FindBy(id = "nav-search-submit-button")
	private WebElement submitButton;

	@FindBy(css = "div.autocomplete-results-container div.s-suggestion")
	private List<WebElement> suggestions;

	public SearchBarComponent(WebDriver driver) {
		this.driver = driver;
		this.wait = new WaitUtils(driver, 15);
		PageFactory.initElements(driver, this);
	}

	public SearchBarComponent type(String text) {
		WebElement box = wait.visible(searchBox);
		box.clear();
		box.sendKeys(text);
		return this;
	}

	public void submitSearch() {
		wait.visible(submitButton).click();
	}

	public int getSuggestionsSize() {
		try {
			wait.visibleAll(suggestions);
			return suggestions.size();
		} catch (Exception e) {
			return 0;
		}
	}

	public String getSuggestionText(int index) {
		if (suggestions == null)
			return "";
		return suggestions.get(index).getText();
	}

	public boolean clickSuggestionByIndex(int index) {
		if (suggestions == null)
			return false;
		try {
			wait.clickable(suggestions.get(index)).click();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
