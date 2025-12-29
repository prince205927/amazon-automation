package com.amazon.automation.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.amazon.automation.base.BasePage;
import com.amazon.automation.tests.models.ProductData;

public class CartPage extends BasePage {

	private final By activeCart = By.cssSelector("div#sc-active-cart");

	private final By cartLink = By.cssSelector("a#nav-cart");

	private final By cartItems = By.cssSelector("div.sc-list-item[role='listitem'], div.sc-list-item");

	private final By productTitles = By.cssSelector("span.sc-product-title");

	private final By productUnitPrices = By.cssSelector("div.sc-badge-price-to-pay span[aria-hidden='true']");

	private final By productQuantities = By.cssSelector("span[data-a-selector='inner-value']");

	private final By cartBadgeCount = By.cssSelector("span#nav-cart-count");

	private final By deleteButtons = By.cssSelector("input[data-action='delete-active']");

	private final By incrementButtons = By.cssSelector("button[data-a-selector='increment']");

	private final By decrementButtons = By.cssSelector("button[data-a-selector='decrement']");
	
	private final By spinner = By.cssSelector("span.a-spinner.a-spinner-small");
	public CartPage(WebDriver driver) {
		super(driver);
	}

	public CartPage waitForResults() {
		wait.visible(activeCart);
		return this;
	}

	public void navigateToCart() {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", wait.clickable(cartLink));
		wait.urlContains("/cart");
	}

	public List<ProductData> getAllCartProducts() {
		List<ProductData> cartProducts = new ArrayList<>();
		int itemCount = getCartItemCount();

		for (int i = 0; i < itemCount; i++) {
			ProductData product = new ProductData();
			product.setTitle(getProductTitleByIndex(i));
			product.setPrice(getUnitPriceByIndex(i));
			product.setQuantity(getQuantityByIndex(i));
			cartProducts.add(product);
		}

		return cartProducts;
	}

	public int getCartItemCount() {
		return driver.findElements(cartItems).size();
	}

	public String getProductTitleByIndex(int index) {
		List<WebElement> titles = driver.findElements(productTitles);
		if (index < 0 || index >= titles.size()) {
			throw new IndexOutOfBoundsException("Invalid product index: " + index);
		}
		return titles.get(index).getAttribute("innerText").trim();
	}

	public Double getUnitPriceByIndex(int index) {
		List<WebElement> prices = driver.findElements(productUnitPrices);
		if (index < 0 || index >= prices.size()) {
			throw new IndexOutOfBoundsException("Invalid product index: " + index);
		}
		String text = prices.get(index).getAttribute("innerText").replace("$", "").replace(",", "").trim();
		return Double.parseDouble(text);
	}

//	public Integer getQuantityByIndex(int index) {
//		List<WebElement> quantities = driver.findElements(productQuantities);
//		if (index < 0 || index >= quantities.size()) {
//			throw new IndexOutOfBoundsException("Invalid product index: " + index);
//		} 
//		return Integer.parseInt(quantities.get(index).getText());
//	}
	public Integer getQuantityByIndex(int index) {
	    List<WebElement> quantities = driver.findElements(productQuantities);
	    if (index < 0 || index >= quantities.size()) {
	        throw new IndexOutOfBoundsException("Invalid product index: " + index);
	    }
	    
	    WebElement qtyElement = quantities.get(index);
	    
	    // Wait until text is non-empty and parsable
	    wait.waitUntil(driver -> {
	        String text = qtyElement.getText().trim();
	        return !text.isEmpty() && text.matches("\\d+");
	    });
	    
	    String text = qtyElement.getText().trim();
	    return Integer.parseInt(text);
	}

	public boolean isCartEmpty() {
		String text = wait.presenceOfElement(cartBadgeCount).getAttribute("innerText").trim();
		return Integer.parseInt(text) == 0;
	}

	public Integer getCartBadgeCount() {
		String text = wait.presenceOfElement(cartBadgeCount).getAttribute("innerText").trim();
		return Integer.parseInt(text);
	}

	public void removeItemByIndex(int index) {
		try {
			List<WebElement> buttons = driver.findElements(deleteButtons);
			if (index < 0 || index >= buttons.size()) {
				throw new IndexOutOfBoundsException("Invalid product index: " + index);
			}
			WebElement button = buttons.get(index);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
		} catch (Exception e) {
		}
	}

	public void removeAllItems() {
		try {
			int count = getCartItemCount();
			for (int i = 0; i < count; i++) {
				removeItemByIndex(i);
				wait.visible(By.xpath("//span[text()[contains(.,'was removed from Shopping Cart')]]"));
			}
		} catch (Exception e) {
		}
	}

	public boolean isProductInCart(String productTitle) {
		try {
			for (WebElement title : driver.findElements(productTitles)) {
				if (title.getAttribute("innerText").trim().contains(productTitle)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public void updateQuantityByIndex(int index, int newQuantity) {
		try {
			int currentQuantity = getQuantityByIndex(index);
			if (newQuantity == 0) {
				removeItemByIndex(index);
				return;
			}

			if (currentQuantity == newQuantity) {
				return;
			}

			boolean increment = newQuantity > currentQuantity;
			int clicks = Math.abs(newQuantity - currentQuantity);
			for (int i = 0; i < clicks; i++) {
				try {
					By selector = increment ? incrementButtons : decrementButtons;
					List<WebElement> buttons = driver.findElements(selector);
					System.out.println("Size of buttons elements"+buttons.size());
					if (index >= buttons.size()) {
						break;
					}

					WebElement button = buttons.get(index);
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
					
					wait.invisible(spinner);
					wait.waitUntil(driver->{
						int updatedQty = getQuantityByIndex(index);
		                return updatedQty != currentQuantity;
					});

				} catch (StaleElementReferenceException e) {
					i--;
				} catch (Exception e) {
					break;
				}
			}
		} catch (Exception e) {
		}
	}

	public void incrementQuantityByIndex(int index) {
		try {
			List<WebElement> buttons = driver.findElements(incrementButtons);
			int currentQuantity = getQuantityByIndex(index);
			if (index < 0 || index >= buttons.size()) {
				throw new IndexOutOfBoundsException("Invalid product index: " + index);
			}
			WebElement button = buttons.get(index);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
			wait.invisible(spinner);
			wait.waitUntil(driver->{
				int updatedQty = getQuantityByIndex(index);
                return updatedQty != currentQuantity;
			});
		} catch (Exception e) {
		}
	}

	public void decrementQuantityByIndex(int index) {
		try {
			List<WebElement> buttons = driver.findElements(decrementButtons);
			int currentQuantity = getQuantityByIndex(index);
			if (index < 0 || index >= buttons.size()) {
				throw new IndexOutOfBoundsException("Invalid product index: " + index);
			}
			WebElement button = buttons.get(index);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
			wait.invisible(spinner);
			wait.waitUntil(driver->{
				int updatedQty = getQuantityByIndex(index);
                return updatedQty != currentQuantity;
			});
		} catch (Exception e) {
		}
	}

	public boolean canIncrementQuantity(int index) {
		try {
			List<WebElement> buttons = driver.findElements(incrementButtons);
			if (index < 0 || index >= buttons.size()) {
				return false;
			}
			WebElement button = buttons.get(index);
			return button.isEnabled() && !button.getAttribute("class").contains("disabled");
		} catch (Exception e) {
			return false;
		}
	}

	public boolean canDecrementQuantity(int index) {
		try {
			List<WebElement> buttons = driver.findElements(decrementButtons);
			if (index < 0 || index >= buttons.size()) {
				return false;
			}
			WebElement button = buttons.get(index);
			return button.isEnabled() && !button.getAttribute("class").contains("disabled");
		} catch (Exception e) {
			return false;
		}
	}
}
