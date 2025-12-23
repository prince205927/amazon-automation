package com.amazon.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.amazon.automation.base.BasePage;

public class CartPage extends BasePage {
	public CartPage(WebDriver driver) {
		super(driver);
	}
	
	public String getProductTitle() {
		return wait.presenceOfElement(By.cssSelector("span.a-truncate-cut")).getAttribute("innerText");
	}
	
	public Double getUnitPrice() {
		String unitPriceText = wait.presenceOfElement(By.cssSelector("div.sc-badge-price-to-pay span[aria-hidden='true']")).getAttribute("innerText").replace("$", "").replace(",", "").trim();
		return Double.parseDouble(unitPriceText);
	}
	
	public Double getSubTotalPrice() {
		String subTotalPriceText = wait.presenceOfElement(By.cssSelector("span#sc-subtotal-amount-activecart span.sc-price")).getAttribute("innerText").replace("$", "").replace(",", "").trim();
		return Double.parseDouble(subTotalPriceText);
	}
	
	public Integer getQuantity() {
		String quantityText = wait.presenceOfElement(By.cssSelector("span[data-a-selector='inner-value']")).getAttribute("innerText");
		return Integer.parseInt(quantityText);
	}
	
}
