package com.amazon.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.amazon.automation.base.BasePage;

public class CartPage extends BasePage {
	public CartPage(WebDriver driver) {
		super(driver);
	}

	public String getProductTitle() {
		return wait.presenceOfElement(By.cssSelector("span.a-truncate-cut")).getAttribute("innerText");
	}

	public Double getUnitPrice() {
		String unitPriceText = wait
				.presenceOfElement(By.cssSelector("div.sc-badge-price-to-pay span[aria-hidden='true']"))
				.getAttribute("innerText").replace("$", "").replace(",", "").trim();
		return Double.parseDouble(unitPriceText);
	}

	public Double getSubTotalPrice() {
		String subTotalPriceText = wait
				.presenceOfElement(By.cssSelector("span#sc-subtotal-amount-activecart span.sc-price"))
				.getAttribute("innerText").replace("$", "").replace(",", "").trim();
		return Double.parseDouble(subTotalPriceText);
	}

	public Integer getQuantity() {
		WebElement quantityElement = wait.visible(By.cssSelector("span[data-a-selector='inner-value']"));
		return Integer.parseInt(quantityElement.getText());
	}

	public void incrementQuantity() {
		wait.clickable(By.cssSelector("button[data-a-selector='increment']")).click();
		wait.invisible(By.cssSelector("span.a-spinner.a-spinner-small"));
	}

	public void decrementQuantity() {
		wait.clickable(By.cssSelector("button[data-a-selector='decrement']")).click();
		wait.invisible(By.cssSelector("span.a-spinner.a-spinner-small"));

	}
	
	public void removeItemFromCart() {
		wait.clickable(By.cssSelector("input[data-action='delete-active']")).click();
		wait.presenceOfElement(By.className("sc-list-item-removed-msg"));
	}
	
	public Integer getBadgeCount() {
		String badgeCountText = wait.presenceOfElement(By.cssSelector("span#nav-cart-count")).getAttribute("innerText").trim();
		return Integer.parseInt(badgeCountText);
	}
	
	public String getToastMessage() {
		 return wait.presenceOfElement(By.xpath("//span[contains(@id,'sc-list-item-removed-msg-text-delete')]")).getAttribute("innerText").trim();
	}
	

}
