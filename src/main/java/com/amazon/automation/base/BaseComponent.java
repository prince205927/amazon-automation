package com.amazon.automation.base;

import org.openqa.selenium.WebDriver;

import com.amazon.automation.utils.WaitUtils;

public abstract class BaseComponent {
	protected final WebDriver driver;
	protected final WaitUtils wait;

	protected BaseComponent(WebDriver driver) {
		this.driver = driver;
		this.wait = new WaitUtils(driver, 15);
	}
}
