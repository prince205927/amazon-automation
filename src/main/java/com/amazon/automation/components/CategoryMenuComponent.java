package com.amazon.automation.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.amazon.automation.base.BaseComponent;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

public class CategoryMenuComponent extends BaseComponent {

	// static locators
	private final By allMenuButton = By.id("nav-hamburger-menu");
	private final By menuContent = By.cssSelector("div#hmenu-content");

	private final By shopByDepartmentCategories = By
			.cssSelector("section[aria-labelledby='Shop by Department'] li a[role='button'] div");

	private final By shopByDepartmentSeeAll = By
			.cssSelector("section[aria-labelledby='Shop by Department'] a[aria-label='See all']");

	private final By visibleMenu = By.cssSelector("div.hmenu.hmenu-visible.hmenu-translateX");

	private final By leftMenu = By.cssSelector("div.hmenu.hmenu-translateX-left");

	private final By menuCanvas = By.cssSelector("div#hmenu-canvas.hmenu-translateX");

	private String lastClickedCategoryText;

	public CategoryMenuComponent(WebDriver driver) {
		super(driver);
		LoggerUtil.info("CategoryMenuComponent initialized");
		ExtentReportLogger.info("Category menu component loaded");

	}

	// menu actions
	public CategoryMenuComponent openMenu() {
		LoggerUtil.info("Attempting to open category menu");
		ExtentReportLogger.logStep("Opening category menu");
		try {
			if (isMenuOpen())
				LoggerUtil.info("Category menu already open");
			ExtentReportLogger.info("Category menu already open");

			return this;
		} catch (Exception e) {
			LoggerUtil.warn("Exception while checking menu state");
		}
		wait.clickable(allMenuButton).click();
		wait.clickable(menuContent);

		LoggerUtil.info("Category menu opened successfully");
		ExtentReportLogger.pass("Category menu opened");

		return this;
	}

	public boolean isMenuOpen() {
		try {
			return wait.visible(menuContent).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	// category locators
	private By categoryByText(String categoryText) {
		return By.xpath("//a[contains(@class,'hmenu-item') and .//div[normalize-space()='" + categoryText + "']]");
	}

	private By categoryFallbackByText(String categoryText) {
		return By.xpath("//a[contains(@class,'hmenu-item') and normalize-space()='" + categoryText + "']");
	}

	private By subCategoryByText(String subCategoryText) {
		return By
				.xpath("//a[contains(@class,'hmenu-item') and contains(normalize-space(), '" + subCategoryText + "')]");
	}

	private By subCategoriesByLastClickedCategory() {
		return By.xpath("//section[contains(@aria-labelledby, \"" + lastClickedCategoryText + "\")]//li//a");
	}

	// category actions
	public WebElement getCategory(String categoryText) {
		try {
			LoggerUtil.info("Locating category: " + categoryText);
			return driver.findElement(categoryByText(categoryText));
		} catch (Exception e) {
			LoggerUtil.warn("Primary locator failed, using fallback for category: " + categoryText);
			return driver.findElement(categoryFallbackByText(categoryText));
		}
	}

	private WebElement getSubCategory(String subCategoryText) {
		LoggerUtil.info("Locating subcategory: " + subCategoryText);
		WebElement subCategory = driver.findElement(subCategoryByText(subCategoryText));
		scrollIntoViewWithinMenu(subCategory);
		return subCategory;
	}

	public CategoryMenuComponent clickCategory(String categoryText) {
		LoggerUtil.info("Clicking category: " + categoryText);
		ExtentReportLogger.logStep("Clicking category: " + categoryText);
		WebElement category = wait.clickable(getCategory(categoryText));
		category.click();

		LoggerUtil.info("Category clicked: " + categoryText);
		ExtentReportLogger.pass("Category clicked: " + categoryText);
		waitForCategoryTransition();
		return this;
	}

	public CategoryMenuComponent clickCategoryByIndex(int index) {
		LoggerUtil.info("Clicking category by index: " + index);
		ExtentReportLogger.logStep("Selecting category by index: " + index);
		List<WebElement> categories = driver.findElements(shopByDepartmentCategories);

		if (index > 3) {
			LoggerUtil.info("Index > 3, expanding categories using 'See all'");
			wait.clickable(shopByDepartmentSeeAll).click();
			categories = driver.findElements(shopByDepartmentCategories);
		}

		wait.clickable(categories.get(index)).click();
		lastClickedCategoryText = categories.get(index).getAttribute("innerText");

		LoggerUtil.info("Category selected: " + lastClickedCategoryText);
		ExtentReportLogger.info("Category selected: " + lastClickedCategoryText);

		waitForCategoryTransition();
		return this;
	}

	public String clickSubCategoryByIndex(int index) {

		LoggerUtil.info("Clicking subcategory by index: " + index);
		ExtentReportLogger.logStep("Selecting subcategory by index: " + index);

		List<WebElement> subCategories = driver.findElements(subCategoriesByLastClickedCategory());

		if (index >= subCategories.size()) {
			throw new RuntimeException("Index out of range. Found only " + subCategories.size() + " subcategories.");
		}

		WebElement subCategory = subCategories.get(index);
		String subCategoryTitle = subCategory.getAttribute("innerText");
		jsClick(subCategory);

		LoggerUtil.info("Subcategory clicked: " + subCategoryTitle);
		ExtentReportLogger.pass("Navigated to subcategory: " + subCategoryTitle);

		return subCategoryTitle;
	}

	// waits and transitions
	private void waitForCategoryTransition() {

		LoggerUtil.info("Waiting for category transition animations");

		wait.visible(visibleMenu);
		System.out.println("Visible menu waited");
		wait.waitForCSSTransitionToComplete(visibleMenu);
		wait.waitForCSSTransitionToComplete(leftMenu);

		LoggerUtil.info("Category transition completed");
		ExtentReportLogger.info("Menu animation completed");
	}

	public CategoryMenuComponent waitForMenuToOpen() {

		LoggerUtil.info("Waiting for menu canvas to be visible");

		wait.visible(menuCanvas);
		wait.waitForCSSTransitionToComplete(menuCanvas);

		LoggerUtil.info("Menu canvas fully loaded");

		return this;
	}

	private void waitForTransformAnimationsToComplete() {
		wait.waitUntil(driver -> {
			try {
				String transform = driver.findElement(visibleMenu).getCssValue("transform");
				return transform != null && !transform.equals("none");
			} catch (Exception e) {
				return false;
			}
		});

		wait.waitUntil(driver -> {
			try {
				String transform = driver.findElement(leftMenu).getCssValue("transform");
				return transform != null && !transform.equals("none");
			} catch (Exception e) {
				return false;
			}
		});
	}

	// navigation helpers

	public void clickSubCategory(String subCategoryText) {

		LoggerUtil.info("Clicking subcategory: " + subCategoryText);
		ExtentReportLogger.logStep("Clicking subcategory: " + subCategoryText);

		WebElement subCategory = wait.clickable(getSubCategory(subCategoryText));
		jsClick(subCategory);

		ExtentReportLogger.pass("Subcategory clicked: " + subCategoryText);

	}

	public void navigateTo(String categoryText, String subCategoryText) {

		LoggerUtil.info("Navigating to Category: " + categoryText + " | Subcategory: " + subCategoryText);
		ExtentReportLogger.logStep("Navigating through menu");

		clickCategory(categoryText);
		if (subCategoryText != null && !subCategoryText.isEmpty()) {
			clickSubCategory(subCategoryText);
		}
	}

	private void scrollIntoViewWithinMenu(WebElement element) {
		try {
			jsScrollIntoView(element);
		} catch (Exception e) {
		}
	}
}
