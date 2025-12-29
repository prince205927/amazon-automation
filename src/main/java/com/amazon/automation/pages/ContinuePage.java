package com.amazon.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.amazon.automation.base.BasePage;
import com.amazon.automation.utils.WaitUtils;

public class ContinuePage extends BasePage {

	private final WaitUtils wait;

	private final By continueToShoppingBtn = By
			.xpath("//button[normalize-space()='Continue to Shopping' or normalize-space()='Continue shopping']");

	public ContinuePage(WebDriver driver) {
		super(driver);
		this.wait = new WaitUtils(driver, 10);
	}

	public boolean isDisplayed() {
		try {
			wait.visible(continueToShoppingBtn);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public HomePage continueShopping() {
		wait.clickable(continueToShoppingBtn).click();
		return new HomePage(driver);
	}
}