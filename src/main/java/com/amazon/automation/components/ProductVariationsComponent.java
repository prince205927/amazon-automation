package com.amazon.automation.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.amazon.automation.base.BaseComponent;

public class ProductVariationsComponent extends BaseComponent {
	public ProductVariationsComponent(WebDriver driver) {
		super(driver);
	}

	public String chooseColor(String text) {
		List<WebElement> availableColorsImg = driver
				.findElements(By.cssSelector("span.image-swatch-button-with-slots img.swatch-image"));
		WebElement colorText = wait
				.presenceOfElement(By.cssSelector("span#inline-twister-expanded-dimension-text-color_name"));
		for (WebElement colorImg : availableColorsImg) {
			String altText = colorImg.getAttribute("alt").trim();
			if (text.equalsIgnoreCase(altText)) {
				// img is not clickable so need to click previous div
				WebElement clickableDiv = colorImg
						.findElement(By.xpath("./ancestor::span[contains(@class,'image-swatch-button-with-slots')]"));
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickableDiv);
				wait.presenceInElement(colorText, text);
				return altText;
			}
		}
		return null;
	}

	public String chooseSize(String text) {
		List<WebElement> availableSizes = driver.findElements(By.cssSelector("span.swatch-title-text-display"));
		WebElement sizeName = wait
				.presenceOfElement(By.cssSelector("span#inline-twister-expanded-dimension-text-size_name"));
		for (WebElement size : availableSizes) {
			String sizeText = size.getAttribute("innerText").trim();
			if (text.equalsIgnoreCase(sizeText)) {
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", size);
				wait.presenceInElement(sizeName, text);
				return sizeText;
			}
		}

		return null;
	}
	
	public Double getPrice() {
	    WebElement priceWhole = driver.findElement(By.cssSelector("span.priceToPay span.a-price-whole"));
	    WebElement priceFraction = driver.findElement(By.cssSelector("span.priceToPay span.a-price-fraction"));

	    String whole = priceWhole.getText().trim();   
	    String fraction = priceFraction.getText().trim(); 

	    String priceString = whole + "." + fraction;      
	    double price = Double.parseDouble(priceString);

	    return price;
	}
	
	
	public boolean isBlank() {
		 WebElement priceWhole = driver.findElement(By.cssSelector("span.priceToPay span.a-price-whole"));
		    WebElement priceFraction = driver.findElement(By.cssSelector("span.priceToPay span.a-price-fraction"));
		    String whole = priceWhole.getText().trim();   
		    String fraction = priceFraction.getText().trim(); 
		 return whole.isEmpty() && fraction.isEmpty();
	}
	
	public String getAvailabilityStatus() {
		WebElement availabilityElement = driver.findElement(By.cssSelector("div#availability span.a-size-medium"));
		String availabilityText = availabilityElement.getAttribute("innerText").trim();
		return availabilityText;
	}
	
	public boolean isAvailabilityStatusBlank() {
		WebElement availabilityElement = driver.findElement(By.cssSelector("div#availability span.a-size-medium"));
		String availabilityText = availabilityElement.getAttribute("innerText").trim();
		return availabilityText.isEmpty();
	}
	

	
	
}
