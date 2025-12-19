package com.amazon.automation.components;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import com.amazon.automation.base.BaseComponent;

public class CategoryMenuComponent extends BaseComponent {
	@FindBy(id = "nav-hamburger-menu")
	private WebElement allMenuButton;

	@FindBy(css = "div#hmenu-content")
	private WebElement menuContent;

	public CategoryMenuComponent(WebDriver driver) {
		super(driver);
	}

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

	public WebElement getCategory(String categoryText) {
		try {
			return driver.findElement(By
					.xpath("//a[contains(@class,'hmenu-item') and .//div[normalize-space()='" + categoryText + "']]"));
		} catch (Exception e) {
			return driver.findElement(
					By.xpath("//a[contains(@class,'hmenu-item') and normalize-space()='" + categoryText + "']"));
		}
	}

	private WebElement getSubCategory(String subCategoryText) {
		WebElement subCategory = driver.findElement(By.xpath(
				"//a[contains(@class,'hmenu-item') and contains(normalize-space(), '" + subCategoryText + "')]"));
		scrollIntoViewWithinMenu(subCategory);
		return subCategory;
	}

	public CategoryMenuComponent clickCategory(String categoryText) {
		WebElement category = wait.clickable(getCategory(categoryText));
		category.click();
		waitForCategoryTransition();
		return this;
	}

	private void waitForCategoryTransition() {
		By visibleMenu = By.cssSelector("div.hmenu.hmenu-visible.hmenu-translateX");
		By leftMenu = By.cssSelector("div.hmenu.hmenu-translateX-left");

		// waiting for visible menu
		wait.visible(visibleMenu);

		// waiting for transitions to complete
		wait.waitForCSSTransitionToComplete(visibleMenu);
		wait.waitForCSSTransitionToComplete(leftMenu);
	}

	public CategoryMenuComponent waitForMenuToOpen() {
		// waiting for menu canvas to be visible
		wait.visible(By.cssSelector("div#hmenu-canvas.hmenu-translateX"));

		// waiting for opening animation to complete
		wait.waitForCSSTransitionToComplete(By.cssSelector("div#hmenu-canvas.hmenu-translateX"));

		return this;
	}

	private void waitForTransformAnimationsToComplete() {
		// waiting for visible animation to complete
		wait.waitUntil(driver -> {
			try {
				WebElement visibleMenu = driver.findElement(By.cssSelector("div.hmenu.hmenu-visible.hmenu-translateX"));
				String transform = visibleMenu.getCssValue("transform");
				return transform != null && !transform.equals("none");
			} catch (Exception e) {
				return false;
			}
		});

		// waiting for left menu animation to complete
		wait.waitUntil(driver -> {
			try {
				WebElement leftMenu = driver.findElement(By.cssSelector("div.hmenu.hmenu-translateX-left"));
				String transform = leftMenu.getCssValue("transform");
				return transform != null && !transform.equals("none");
			} catch (Exception e) {
				return false;
			}
		});
	}

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