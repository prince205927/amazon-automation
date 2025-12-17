package com.amazon.automation.utils;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
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
	
	public List<WebElement> visibleAll(List<WebElement> elements){
		wait.until(ExpectedConditions.visibilityOfAllElements(elements));
		return elements;
	}
	
	public WebElement untilVisible(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}
	
	public WebElement presenceOfElement(By locator) {
	    return presenceOfElement(locator, 15);
	}
	
	public WebElement presenceOfElement(By locator, int timeoutSeconds) {
	    return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
	}
	
	public WebElement clickable(By locator) {
	    return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	public WebElement visible(By locator) {
	    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}


	
}
