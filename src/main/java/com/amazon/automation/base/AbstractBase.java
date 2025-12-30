
package com.amazon.automation.base;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.amazon.automation.utils.WaitUtils;

public abstract class AbstractBase {

	protected final WebDriver driver;
	protected final WaitUtils wait;

	protected AbstractBase(WebDriver driver) {
		this.driver = driver;
		this.wait = new WaitUtils(driver, 15);
	}

	protected void jsClick(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
	}

	protected void jsClick(By locator) {
		jsClick(driver.findElement(locator));
	}

	protected void jsSetValueWithInput(WebElement element, Object value) {
		((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];"
				+ "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));", element, value);
	}

	protected void jsSetValueWithInput(By locator, Object value) {
		jsSetValueWithInput(driver.findElement(locator), value);
	}

	protected void jsSetValueWithInputAndChange(WebElement element, Object value) {
		((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];"
				+ "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));"
				+ "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));", element, value);
	}

	protected void jsSetValueWithInputAndChange(By locator, Object value) {
		jsSetValueWithInputAndChange(driver.findElement(locator), value);
	}

	protected void jsScrollIntoView(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'auto', block: 'center'});",
				element);
	}

	protected void jsScrollIntoView(By locator) {
		jsScrollIntoView(driver.findElement(locator));
	}

	protected void jsScrollIntoViewTop(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}

	protected void jsScrollIntoViewTop(By locator) {
		jsScrollIntoViewTop(driver.findElement(locator));
	}

	protected void jsScrollAndClick(WebElement element) {
		jsScrollIntoViewTop(element);
		jsClick(element);
	}

	protected void jsSetValueWithChange(WebElement element, Object value) {
		((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];"
				+ "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));", element, value);
	}

	protected void jsSetValueWithChange(By locator, Object value) {
		jsSetValueWithChange(driver.findElement(locator), value);
	}

	protected void jsScrollIntoViewCenter(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
	}

	protected void jsScrollIntoViewCenter(By locator) {
		jsScrollIntoViewCenter(driver.findElement(locator));
	}

	protected void jsClickWhenClickable(By locator) {
		WebElement element = wait.clickable(locator);
		jsClick(element);
	}

	protected void jsClickWhenPresent(By locator) {
		WebElement element = wait.presenceOfElement(locator);
		jsClick(element);
	}

	protected Object executeJs(String script, Object... args) {
		return ((JavascriptExecutor) driver).executeScript(script, args);
	}

}
