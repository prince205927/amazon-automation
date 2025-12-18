package com.amazon.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.amazon.automation.base.BasePage;
import com.amazon.automation.components.CategoryMenuComponent;
import com.amazon.automation.components.FilterPanelComponent;
//import com.amazon.automation.components.FilterPanelComponent;
import com.amazon.automation.components.SearchBarComponent;

public class HomePage extends BasePage {

	public HomePage(WebDriver driver) {
		super(driver);
	}
	
	public SearchBarComponent searchBar() {
		return new SearchBarComponent(driver);
	}
	
	public CategoryMenuComponent categoryMenu() {
		return new CategoryMenuComponent(driver);
	}
	
	public FilterPanelComponent filters() {
		return new FilterPanelComponent(driver);
	}
}
