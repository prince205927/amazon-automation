package com.amazon.automation.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.amazon.automation.base.BaseComponent;

public class CategoryMenuComponent extends BaseComponent {
	
	//static locators
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
	}

	//menu actions
	public CategoryMenuComponent openMenu() {
		try {
			if (isMenuOpen())
				return this;
		} catch (Exception e) {
		}
		wait.clickable(allMenuButton).click();
		wait.clickable(menuContent);
		return this;
	}

	public boolean isMenuOpen() {
		try {
			return wait.visible(menuContent).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	//category locators
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

	
	//category actions
	public WebElement getCategory(String categoryText) {
		try {
			return driver.findElement(categoryByText(categoryText));
		} catch (Exception e) {
			return driver.findElement(categoryFallbackByText(categoryText));
		}
	}

	private WebElement getSubCategory(String subCategoryText) {
		WebElement subCategory = driver.findElement(subCategoryByText(subCategoryText));
		scrollIntoViewWithinMenu(subCategory);
		return subCategory;
	}

	public CategoryMenuComponent clickCategory(String categoryText) {
		WebElement category = wait.clickable(getCategory(categoryText));
		category.click();
		waitForCategoryTransition();
		return this;
	}

	public CategoryMenuComponent clickCategoryByIndex(int index) {
		List<WebElement> categories = driver.findElements(shopByDepartmentCategories);

		if (index > 3) {
			wait.clickable(shopByDepartmentSeeAll).click();
			categories = driver.findElements(shopByDepartmentCategories);
		}

		wait.clickable(categories.get(index)).click();
		lastClickedCategoryText = categories.get(index).getAttribute("innerText");
		waitForCategoryTransition();
		return this;
	}

	public String clickSubCategoryByIndex(int index) {
		List<WebElement> subCategories = driver.findElements(subCategoriesByLastClickedCategory());

		if (index >= subCategories.size()) {
			throw new RuntimeException("Index out of range. Found only " + subCategories.size() + " subcategories.");
		}

		WebElement subCategory = subCategories.get(index);
		String subCategoryTitle = subCategory.getAttribute("innerText");
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", subCategory);
		return subCategoryTitle;
	}

	//waits and transitions
	private void waitForCategoryTransition() {
		wait.visible(visibleMenu);
		wait.waitForCSSTransitionToComplete(visibleMenu);
		wait.waitForCSSTransitionToComplete(leftMenu);
	}

	public CategoryMenuComponent waitForMenuToOpen() {
		wait.visible(menuCanvas);
		wait.waitForCSSTransitionToComplete(menuCanvas);
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

	//navigation helpers

	public void clickSubCategory(String subCategoryText) {
		WebElement subCategory = wait.clickable(getSubCategory(subCategoryText));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", subCategory);
	}

	public void navigateTo(String categoryText, String subCategoryText) {
		clickCategory(categoryText);
		if (subCategoryText != null && !subCategoryText.isEmpty()) {
			clickSubCategory(subCategoryText);
		}
	}

	private void scrollIntoViewWithinMenu(WebElement element) {
		try {
			((JavascriptExecutor) driver)
					.executeScript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center'});", element);
		} catch (Exception e) {
		}
	}
}
