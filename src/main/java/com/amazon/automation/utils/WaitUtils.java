package com.amazon.automation.utils;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {
	private final WebDriverWait wait;

	public WaitUtils(WebDriver driver, int seconds) {
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
	}

	public WebElement visible(WebElement element) {
		return wait.until(ExpectedConditions.visibilityOf(element));
	}

	public WebElement clickable(WebElement element) {
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}
}
