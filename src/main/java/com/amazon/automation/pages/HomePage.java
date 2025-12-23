package com.amazon.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import com.amazon.automation.base.BasePage;
import com.amazon.automation.components.CategoryMenuComponent;
import com.amazon.automation.components.FilterPanelComponent;
//import com.amazon.automation.components.FilterPanelComponent;
import com.amazon.automation.components.SearchBarComponent;
import com.amazon.automation.components.SortingComponent;

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

	public SortingComponent sorts() {
		return new SortingComponent(driver);
	}
	public void changeLocation(String text) {
		WebElement locationModalButton = wait.presenceOfElement(By.id("nav-global-location-popover-link"));
		wait.clickable(locationModalButton);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", locationModalButton);
		wait.presenceOfElement(By.cssSelector("div.a-popover-wrapper"));
		WebElement selectLocationField = wait.presenceOfElement(By.id("GLUXCountryList"));
		Select select = new Select(selectLocationField);
		select.selectByVisibleText(text);
		WebElement doneButton = wait.presenceOfElement(By.cssSelector("button[name='glowDoneButton']"));
		wait.clickable(doneButton);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", doneButton);
		wait.staleness(doneButton);
	    wait.visible(By.id("nav-logo-sprites"));
	}
}
