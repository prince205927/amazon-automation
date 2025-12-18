package com.amazon.automation.components;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.amazon.automation.base.BaseComponent;
import com.amazon.automation.utils.WaitUtils;

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

	// getting category element
	public WebElement getCategory(String categoryText) {
		try {
			return driver.findElement(By
					.xpath("//a[contains(@class,'hmenu-item') and .//div[normalize-space()='" + categoryText + "']]"));
		} catch (Exception e) {
			return driver.findElement(
					By.xpath("//a[contains(@class,'hmenu-item') and normalize-space()='" + categoryText + "']"));
		}
	}

	// getting subcategory element
	private WebElement getSubCategory(String subCategoryText) {

		WebElement subCategory = driver.findElement(By.xpath(
				"//a[contains(@class,'hmenu-item') and contains(normalize-space(), '" + subCategoryText + "')]"));

		// Scroll into view if needed
		scrollIntoViewWithinMenu(subCategory);

		return subCategory;
	}

	public CategoryMenuComponent clickCategory(String categoryText) {
		WebElement category = getCategory(categoryText);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		wait.clickable(category).click();
		return this;
	}

	public void clickSubCategory(String subCategoryText) {
		WebElement subCategory = getSubCategory(subCategoryText);
		wait.clickable(subCategory);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", subCategory); // here using .click will
																							// result to
																							// ElementIntercepted
																							// problem
	}

	public void navigateTo(String categoryText, String subCategoryText) {
		clickCategory(categoryText);
		if (subCategoryText != null && !subCategoryText.isEmpty()) {
			clickSubCategory(subCategoryText);
		}
	}

	// scrolls into view within the hamburger container
	private void scrollIntoViewWithinMenu(WebElement element) {
		try {
			((JavascriptExecutor) driver)
					.executeScript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center'});", element);
		} catch (Exception e) {
		}
	}

}
