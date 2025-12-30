package com.amazon.automation.pages;

import org.openqa.selenium.By;
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
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

public class HomePage extends BasePage {

	private final By locationModalButton = By.id("nav-global-location-popover-link");
	private final By locationPopoverWrapper = By.cssSelector("div.a-popover-wrapper");
	private final By selectLocationField = By.id("GLUXCountryList");
	private final By doneButton = By.cssSelector("button[name='glowDoneButton']");
	private final By logo = By.id("nav-logo-sprites");

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

		LoggerUtil.info("Changing shopping location to: " + text);
		ExtentReportLogger.logStep("Changing delivery location to: " + text);

		WebElement locationBtn = wait.presenceOfElement(locationModalButton);
		wait.clickable(locationBtn);
		jsClick(locationBtn);

		wait.presenceOfElement(locationPopoverWrapper);

		WebElement countrySelectField = wait.presenceOfElement(selectLocationField);
		Select select = new Select(countrySelectField);
		select.selectByVisibleText(text);

		WebElement doneBtn = wait.presenceOfElement(doneButton);
		wait.clickable(doneBtn);
		jsClick(doneBtn);

		wait.staleness(doneBtn);
		wait.visible(logo);

		LoggerUtil.info("Location changed successfully to: " + text);
		ExtentReportLogger.pass("Delivery location updated successfully");

	}
}