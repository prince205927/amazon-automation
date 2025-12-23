package com.amazon.automation.pages;

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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.amazon.automation.base.BasePage;
import com.amazon.automation.components.ModalComponent;
import com.amazon.automation.components.ProductVariationsComponent;

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
		List<WebElement> images = driver.findElements(By.cssSelector("li.imageThumbnail img"));
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
		WebElement availabilityText = wait.presenceOfElement(By.cssSelector("div#availability span.a-color-success"));
		return !availabilityText.getAttribute("innerText").trim().isEmpty();
	}

	public void clickToSeeFullView() {
		WebElement fullView = wait.presenceOfElement(By.partialLinkText("Click to see full view"));
		wait.clickable(fullView).click();
		waitForTransformToComplete();
	}

	public void waitForTransformToComplete() {
		wait.visible(By.cssSelector("div.a-popover.a-popover-modal.a-declarative.a-popover-modal-fixed-height"));
		wait.waitUntil(driver -> {
			try {
				WebElement transformingDiv = driver.findElement(
						By.cssSelector("div.a-popover.a-popover-modal.a-declarative.a-popover-modal-fixed-height"));
				String transform = transformingDiv.getCssValue("transform");
				return transform != null && !transform.equals("none");
			} catch (Exception e) {
				return false;
			}
		});
		wait.presenceOfElement(By.className("fullscreen"));
	}

	public ModalComponent goToModal() {
		return new ModalComponent(driver);
	}

	public ProductVariationsComponent goToVariations() {
		return new ProductVariationsComponent(driver);
	}

	public String getProductName() {
		WebElement productNameElement = driver.findElement(By.id("productTitle"));
		String productName = productNameElement.getAttribute("innerText").trim();
		return productName;
	}

	public void selectQuantity(String quantity) {
		WebElement selectElement = driver.findElement(By.cssSelector("select#quantity"));
		Select select = new Select(selectElement);
		select.selectByValue(quantity);
	}
	
	public Integer getQuantity() {
		WebElement selectElement = driver.findElement(By.cssSelector("select#quantity"));
		Select select = new Select(selectElement);
		String selectedText = select.getFirstSelectedOption().getText();
		return Integer.parseInt(selectedText);
	}
	public CartPage addToCart() {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();",
				wait.presenceOfElement(By.cssSelector("input#add-to-cart-button")));
		wait.visible(By.xpath("//h1[contains(.,'Added to cart')]"));
		((JavascriptExecutor) driver).executeScript("arguments[0].click();",
				wait.presenceOfElement(By.xpath("(//a[contains(.,'Go to Cart')])[1]")));
		wait.urlContains("/cart");
		return new CartPage(driver);
	}

}
