package com.amazon.automation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.amazon.automation.base.BasePage;

public class ProductDetailsPage extends BasePage {
	public ProductDetailsPage(WebDriver driver) {
		super(driver);
	}

	public ProductDetailsPage waitForResults() {
		wait.visible(By.id("ppd"));
		return this;
	}

	public boolean hasTitle() {
		return !driver.findElement(By.id("productTitle")).getText().trim().isEmpty();
	}

	public boolean hasImages() {
		WebElement landingImage = driver.findElement(By.id("landingImage"));
		List<WebElement> images = driver.findElements(By.cssSelector("span.a-button-thumbnail img"));
		return landingImage.isDisplayed() && images.get(0).getAttribute("src") != null;
	}

	public boolean hasValidRatings() {
		List<WebElement> ratings = driver.findElements(By.cssSelector("a.a-popover-trigger span.a-icon-alt"));
		if (ratings.isEmpty()) {
			System.out.println("No ratings");
			return false;
		}
		// getText didn't work here because text is hidden via CSS
		String partialText = ratings.get(0).getAttribute("innerText").trim().toLowerCase();
		return partialText.contains("out of 5");

	}

	// getText didn't work here because text is hidden via CSS
	public boolean hasAvailabilityText() {
		WebElement availabilityText = driver.findElement(By.cssSelector("div#availability span.a-color-success"));
		return !availabilityText.getAttribute("innerText").trim().isEmpty();
	}
}
