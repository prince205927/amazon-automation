package com.amazon.automation.pages;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.amazon.automation.base.BasePage;
import com.amazon.automation.utils.WaitUtils;

public class SearchResultsPage extends BasePage {
	
	private final WaitUtils wait;
	
	//search term heading 
	@FindBy(css = "span.a-color-state")
	private WebElement searchTermHeader;
	
	//product title links
	@FindBy(css="div[data-component-type='s-search-result']")
	private List<WebElement> titleLinks;
	
	//main container
	@FindBy(css="div.s-main-slot")
	private WebElement mainSlot;
	public SearchResultsPage(WebDriver driver) {
		super(driver);
		this.wait = new WaitUtils(driver, 15);
	}
	
	public SearchResultsPage waitForResults() {
			wait.visible(mainSlot);
			return this;
	}
	
	public boolean hasResults() {
		return titleLinks!=null && !titleLinks.isEmpty();
	}
}
