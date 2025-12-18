package com.amazon.automation.pages;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.amazon.automation.base.BasePage;
import com.amazon.automation.utils.WaitUtils;

public class SearchResultsPage extends BasePage {

	private final WaitUtils wait;

	// search term heading
	@FindBy(css = "span.a-color-state")
	private WebElement searchTermHeader;

	// product title links
	@FindBy(css = "div[data-component-type='s-search-result']")
	private List<WebElement> titleLinks;

	// main container
	@FindBy(css = "div.s-main-slot")
	private WebElement mainSlot;

	@FindBy(css = "h2.a-size-medium")
	private List<WebElement> productTitles;
	public SearchResultsPage(WebDriver driver) {
		super(driver);
		this.wait = new WaitUtils(driver, 15);
	}

	public SearchResultsPage waitForResults() {
		wait.visible(mainSlot);
		return this;
	}

	public boolean hasResults() {
		return titleLinks != null && !titleLinks.isEmpty();
	}

	public String currentSearchTerm() {
		return searchTermHeader.getText();
	}
	
	public boolean checkProductTitles(String brand) {
		String requestedBrand = brand.trim().toLowerCase();
	    List<WebElement> productTitles = driver.findElements(By.cssSelector(
	            "div.s-main-slot div[data-asin][data-component-type='s-search-result'] h2 a span")); /*page factory cannot be used because need to refetch titles after filtering*/
		for(WebElement title: productTitles) {
			String obtainedBrand = title.getText().trim().toLowerCase();
			if(!obtainedBrand.contains(requestedBrand)) {
				System.out.println("The unmatchable title is" + title.getText());
				return false;
			}
		}
		return true;
	}
}
