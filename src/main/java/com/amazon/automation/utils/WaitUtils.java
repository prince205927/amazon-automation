package com.amazon.automation.utils;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
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
	
	public List<WebElement> visibleAll(By locator) {
		return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
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
	
	public boolean notPresent(WebElement element, String text)
	{
		return wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(element, text)));
	}
	public boolean urlContains(String url) {
		return wait.until(ExpectedConditions.urlContains(url));
	}
	public WebElement visible(By locator) {
	    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}
	
	public boolean staleness(WebElement element) {
		return wait.until(ExpectedConditions.stalenessOf(element));
	}
	
	public boolean presenceInElement(WebElement element, String target) {
		return wait.until(ExpectedConditions.textToBePresentInElement(element, target));
	}
	public boolean presenceInElement(By locator, String target) {
		return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, target));
	}
	public boolean waitUntil(ExpectedCondition<?> condition) {
        try {
            wait.until(condition);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
    
    public <T> T waitUntilWithReturn(ExpectedCondition<T> condition) {
        return wait.until(condition);
    }
    
    public void waitForCSSTransitionToComplete(By locator) {
        try {
            wait.until(driver -> {
                try {
                    WebElement element = driver.findElement(locator);
                    Boolean transitionComplete = (Boolean) ((JavascriptExecutor) driver)
                        .executeScript(
                            "var elem = arguments[0];" +
                            "var cs = window.getComputedStyle(elem);" +
                            "var transitionDuration = cs.getPropertyValue('transition-duration');" +
                            "return transitionDuration === '0s' || transitionDuration === '';",
                            element
                        );
                    return transitionComplete;
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (Exception e) {
        }
    }
    
    public boolean invisible(By locator) {
    	return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
}
	
