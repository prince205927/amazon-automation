package com.amazon.automation.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.amazon.automation.utils.WaitUtils;

public abstract class BasePage {
	protected final WebDriver driver;
	protected final WaitUtils wait;

	public BasePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WaitUtils(driver, 15);
	}
}
