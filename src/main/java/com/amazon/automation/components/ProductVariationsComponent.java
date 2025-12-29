package com.amazon.automation.components;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.amazon.automation.base.BaseComponent;
import com.amazon.automation.tests.models.ProductData;

public class ProductVariationsComponent extends BaseComponent {

	private final By colorImages =
			By.cssSelector("span.image-swatch-button-with-slots img.swatch-image");

	private final By selectedColorText =
			By.cssSelector("span#inline-twister-expanded-dimension-text-color_name");

	private final By sizeOptions =
			By.cssSelector("span.swatch-title-text-display");

	private final By selectedSizeText =
			By.cssSelector("span#inline-twister-expanded-dimension-text-size_name");

	private final By priceWhole =
			By.cssSelector("span.priceToPay span.a-price-whole");

	private final By priceFraction =
			By.cssSelector("span.priceToPay span.a-price-fraction");

	private final By availabilityText =
			By.cssSelector("div#availability span.a-size-medium");

	public ProductVariationsComponent(WebDriver driver) {
		super(driver);
	}

	private By colorClickableAncestor() {
		return By.xpath("./ancestor::span[contains(@class,'image-swatch-button-with-slots')]");
	}

	private By sizeClickableAncestor() {
		return By.xpath("./ancestor::div[contains(@class, 'swatch-title-text-container')]");
	}

	public String chooseColor(String text) {
		List<WebElement> availableColorsImg = driver.findElements(colorImages);
		WebElement colorText = wait.presenceOfElement(selectedColorText);

		for (WebElement colorImg : availableColorsImg) {
			String altText = colorImg.getAttribute("alt").trim();
			if (text.equalsIgnoreCase(altText)) {
				WebElement clickableDiv = colorImg.findElement(colorClickableAncestor());
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickableDiv);
				wait.presenceInElement(colorText, text);
				return altText;
			}
		}
		return null;
	}

	public String chooseSize(String text) {
		List<WebElement> availableSizes = driver.findElements(sizeOptions);
		Actions actions = new Actions(driver);

		for (WebElement size : availableSizes) {
			String sizeText = size.getAttribute("innerText").trim();

			if (text.equalsIgnoreCase(sizeText)) {
				WebElement clickableElement = size.findElement(sizeClickableAncestor());
				actions.moveToElement(clickableElement).click().perform();
				wait.presenceInElement(selectedSizeText, text);
				return sizeText;
			}
		}
		return null;
	}

	public Double getPrice() {
		String whole = wait.presenceOfElement(priceWhole).getText().trim();
		String fraction = wait.presenceOfElement(priceFraction).getText().trim();
		return Double.parseDouble(whole + "." + fraction);
	}

	public boolean isBlank() {
		String whole = driver.findElement(priceWhole).getText().trim();
		String fraction = driver.findElement(priceFraction).getText().trim();
		return whole.isEmpty() && fraction.isEmpty();
	}

	public String getAvailabilityStatus() {
		return driver.findElement(availabilityText).getAttribute("innerText").trim();
	}

	public boolean isAvailabilityStatusBlank() {
		return driver.findElement(availabilityText).getAttribute("innerText").trim().isEmpty();
	}

	public String getSelectedColor() {
		return wait.presenceOfElement(selectedColorText).getAttribute("innerText").trim();
	}

	public String getSelectedSize() {
		return wait.presenceOfElement(selectedSizeText).getAttribute("innerText").trim();
	}
}

