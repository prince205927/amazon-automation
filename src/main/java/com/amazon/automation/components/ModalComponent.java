package com.amazon.automation.components;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.amazon.automation.base.BaseComponent;

public class ModalComponent extends BaseComponent {
	public ModalComponent(WebDriver driver) {
		super(driver);
	}
	public boolean isFullViewOpen() {
		return wait.presenceOfElement(By.cssSelector("div.a-popover.a-popover-modal.a-declarative.a-popover-modal-fixed-height")).getCssValue("display").contains("block");
	}
	public Map<String, String> getImageDetails() {
		WebElement landingImage = wait.presenceOfElement(By.className("fullscreen"));
		Map<String, String> details = new LinkedHashMap<>();
		details.put("src", landingImage.getAttribute("src"));
		details.put("alt", landingImage.getAttribute("alt"));
		details.put("data-old-hires", landingImage.getAttribute("data-old-hires"));
		details.put("currentSrc", landingImage.getAttribute("currentSrc"));
		return details;
	}

	public boolean checkSwitchingBehaviour() {
	    List<WebElement> thumbnails = driver.findElements(By.cssSelector("div.ivRow div.ivThumbImage"));
	    
	    if (thumbnails.size() <= 1) {
	        System.out.println("Not enough thumbnails to test switching");
	        return false;
	    }
	    
	    boolean allSwitchesWorked = true;
	    
	    for (int i = 1; i < thumbnails.size(); i++) {
	        Map<String, String> beforeClick = getImageDetails();
	        System.out.println("\n--- Clicking thumbnail " + (i + 1) + " ---");
	        System.out.println("Before click: " + beforeClick.get("src"));
	        
	        WebElement thumbnail = thumbnails.get(i);
	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", thumbnail);
	        
	        WebDriverWait waiter = new WebDriverWait(driver, Duration.ofSeconds(10));
	        try {
	            waiter.until(ExpectedConditions.not(
	                ExpectedConditions.attributeContains(By.className("fullscreen"), "src", beforeClick.get("src"))
	            ));
	            
	            Map<String, String> afterClick = getImageDetails();
	            System.out.println("After click: " + afterClick.get("src"));
	            
	            boolean changed = !beforeClick.get("src").equals(afterClick.get("src"));
	            
	            if (changed) {
	                System.out.println("Thumbnail " + (i + 1) + " switched successfully");
	            } else {
	                System.out.println("Thumbnail " + (i + 1) + " did NOT switch");
	                allSwitchesWorked = false;
	            }
	            
	        } catch (TimeoutException e) {
	            System.out.println("Thumbnail " + (i + 1) + " - timeout waiting for image change");
	            allSwitchesWorked = false;
	        }
	    }
	    
	    return allSwitchesWorked;
	}

	public int getImageHeight() {
		WebElement landingImage = wait.presenceOfElement(By.className("fullscreen"));
		return landingImage.getSize().getHeight();
	}

	public int getImageWidth() {
		WebElement landingImage = wait.presenceOfElement(By.className("fullscreen"));
		return landingImage.getSize().getWidth();
	}

	public void zoomInteraction() {
		WebElement landingImage = wait.presenceOfElement(By.className("fullscreen"));
		int currentHeight = landingImage.getSize().getHeight();

		Actions action = new Actions(driver);
		action.moveToElement(landingImage).perform();

		landingImage = wait.clickable(By.className("fullscreen"));
		landingImage.click();

		// waiting for dimension change to complete
		wait.waitUntil(driver -> {
			try {
				WebElement img = driver.findElement(By.className("fullscreen"));
				int newHeight = img.getSize().getHeight();
				return newHeight != currentHeight;
			} catch (StaleElementReferenceException e) {
				return false;
			}
		});
	}
	
}
