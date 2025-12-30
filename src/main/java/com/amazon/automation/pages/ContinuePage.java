package com.amazon.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.amazon.automation.base.BasePage;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;
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

			LoggerUtil.info("Continue shopping page is displayed");

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public HomePage continueShopping() {

		LoggerUtil.info("Clicking 'Continue shopping' button");
		ExtentReportLogger.logStep("Clicking Continue Shopping button");

		wait.clickable(continueToShoppingBtn).click();

		LoggerUtil.info("Navigating to HomePage after continue shopping");
		ExtentReportLogger.pass("Navigated to Home page");

		return new HomePage(driver);
	}
}