package com.amazon.automation.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

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
		wait.visible(By.cssSelector("div#sc-active-cart"));
		return this;
	}

	public void navigateToCart() {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();",
				wait.clickable(By.cssSelector("a#nav-cart")));
		wait.urlContains("/cart");
	}

	public List<ProductData> getAllCartProducts() {
		List<ProductData> cartProducts = new ArrayList<>();
		int itemCount = getCartItemCount();

		System.out.println("Reading " + itemCount + " items from cart...");

		for (int i = 0; i < itemCount; i++) {
			ProductData product = new ProductData();
			product.setTitle(getProductTitleByIndex(i));
			product.setPrice(getUnitPriceByIndex(i));
			product.setQuantity(getQuantityByIndex(i));
			cartProducts.add(product);

			System.out.println("Cart item " + (i + 1) + ": " + product.getTitle() + " | $" + product.getPrice()
					+ " | Qty: " + product.getQuantity());
		}

		return cartProducts;
	}

	public int getCartItemCount() {
		List<WebElement> cartItems = driver
				.findElements(By.cssSelector("div.sc-list-item[role='listitem'], div.sc-list-item"));
		return cartItems.size();
	}

	public String getProductTitleByIndex(int index) {
		List<WebElement> productTitles = driver.findElements(By.cssSelector("span.sc-product-title"));
		if (index < 0 || index >= productTitles.size()) {
			throw new IndexOutOfBoundsException("Invalid product index: " + index);
		}
		return productTitles.get(index).getAttribute("innerText").trim();
	}

	public Double getUnitPriceByIndex(int index) {
		List<WebElement> productUnitPrices = driver
				.findElements(By.cssSelector("div.sc-badge-price-to-pay span[aria-hidden='true']"));
		if (index < 0 || index >= productUnitPrices.size()) {
			throw new IndexOutOfBoundsException("Invalid product index: " + index);
		}
		String quantityText = productUnitPrices.get(index).getAttribute("innerText").replace("$", "").replace(",", "")
				.trim();
		return Double.parseDouble(quantityText);
	}

	public Integer getQuantityByIndex(int index) {

		List<WebElement> productQuantities = driver.findElements(By.cssSelector("span[data-a-selector='inner-value']"));
		if (index < 0 || index >= productQuantities.size()) {
			throw new IndexOutOfBoundsException("Invalid product index: " + index);
		}
		String quantityText = productQuantities.get(index).getText();
		return Integer.parseInt(quantityText);
	}

	public boolean isCartEmpty() {
		String badgeCountText = wait.presenceOfElement(By.cssSelector("span#nav-cart-count")).getAttribute("innerText").trim();
		int cartItems = Integer.parseInt(badgeCountText);
		return cartItems==0;
	}
	
	public Integer getCartBadgeCount() {
		String badgeCountText = wait.presenceOfElement(By.cssSelector("span#nav-cart-count")).getAttribute("innerText").trim();
		return Integer.parseInt(badgeCountText);
	}

	public void removeItemByIndex(int index) {
	    try {
	        List<WebElement> deleteButtons = driver.findElements(
	            By.cssSelector("input[data-action='delete-active']"));
	        
	        if (index < 0 || index >= deleteButtons.size()) {
	            throw new IndexOutOfBoundsException("Invalid product index: " + index);
	        }
	        
	        WebElement deleteButton = deleteButtons.get(index);
	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", deleteButton);
	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteButton);
	        
	        System.out.println("Removed item at index: " + index);
	    } catch (Exception e) {
	        System.out.println("Error removing item: " + e.getMessage());
	    }
	}

	public void removeAllItems() {
	    try {
	        int itemCount = getCartItemCount();
	        
	        for (int i = 0; i < itemCount; i++) {
	            removeItemByIndex(0);
	        }
	        
	        System.out.println(" All items removed from cart");
	    } catch (Exception e) {
	        System.out.println("Error removing all items: " + e.getMessage());
	    }
	}

	public boolean isProductInCart(String productTitle) {
	    try {
	        List<WebElement> productTitles = driver.findElements(By.cssSelector("span.sc-product-title"));
	        
	        for (WebElement title : productTitles) {
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
        
        System.out.println("Current quantity: " + currentQuantity + ", Target quantity: " + newQuantity);
        
        if (newQuantity == 0) {
            System.out.println("Deleting item at index: " + index);
            removeItemByIndex(index);
            return;
        }
        
        if (currentQuantity == newQuantity) {
            System.out.println("Quantity already at target value");
            return;
        }
        
        boolean needsIncrement = newQuantity > currentQuantity;
        int clicksNeeded = Math.abs(newQuantity - currentQuantity);
        
        System.out.println((needsIncrement ? "Incrementing " : "Decrementing ") + clicksNeeded + " times");
        
        for (int i = 0; i < clicksNeeded; i++) {
            try {
                // refetching buttons each time to avoid stale element reference
                By buttonSelector = needsIncrement ? 
                    By.cssSelector("button[data-a-selector='increment']") :
                    By.cssSelector("button[data-a-selector='decrement']");
                
                List<WebElement> buttons = driver.findElements(buttonSelector);
                
                if (index >= buttons.size()) {
                    System.out.println("Button index out of bounds, stopping at click " + (i + 1));
                    break;
                }
                
                WebElement button = buttons.get(index);
                
                // Scroll to button
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
                
                // Click the button
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
                
                System.out.println("Click " + (i + 1) + " of " + clicksNeeded + " completed");

                int updatedQty = getQuantityByIndex(index);
                System.out.println("Quantity after click: " + updatedQty);
                
            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element on click " + (i + 1) + ", retrying...");
                i--; // retrying this click
            } catch (Exception e) {
                System.out.println("Error on click " + (i + 1) + ": " + e.getMessage());
                break;
            }
        }

        int finalQuantity = getQuantityByIndex(index);
        System.out.println(" Final quantity: " + finalQuantity + " for item at index: " + index);
        
        if (finalQuantity != newQuantity) {
            System.out.println(" Warning: Target quantity was " + newQuantity + 
                " but final quantity is " + finalQuantity);
        }
        
    } catch (Exception e) {
        System.out.println("Error updating quantity: " + e.getMessage());
        e.printStackTrace();
    }
}

	public void incrementQuantityByIndex(int index) {
	    try {
	        List<WebElement> incrementButtons = driver.findElements(
	            By.cssSelector("button[data-a-selector='increment']"));
	        
	        if (index < 0 || index >= incrementButtons.size()) {
	            throw new IndexOutOfBoundsException("Invalid product index: " + index);
	        }
	        
	        WebElement button = incrementButtons.get(index);
	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
	        
	        System.out.println("Incremented quantity for item at index: " + index);
	    } catch (Exception e) {
	        System.out.println("Error incrementing quantity: " + e.getMessage());
	    }
	}

	public void decrementQuantityByIndex(int index) {
	    try {
	        List<WebElement> decrementButtons = driver.findElements(
	            By.cssSelector("button[data-a-selector='decrement']"));
	        
	        if (index < 0 || index >= decrementButtons.size()) {
	            throw new IndexOutOfBoundsException("Invalid product index: " + index);
	        }
	        
	        int currentQty = getQuantityByIndex(index);
	        
	        WebElement button = decrementButtons.get(index);
	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
	        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
	        
	        if (currentQty == 1) {
	            System.out.println("Deleted item at index: " + index + " (quantity was 1)");
	        } else {
	            System.out.println("Decremented quantity for item at index: " + index);
	        }
	        
	    } catch (Exception e) {
	        System.out.println("Error decrementing quantity: " + e.getMessage());
	    }
	}

	public boolean canIncrementQuantity(int index) {
	    try {
	        List<WebElement> incrementButtons = driver.findElements(
	            By.cssSelector("button[data-a-selector='increment']"));
	        
	        if (index < 0 || index >= incrementButtons.size()) {
	            return false;
	        }
	        
	        WebElement button = incrementButtons.get(index);
	        return button.isEnabled() && !button.getAttribute("class").contains("disabled");
	    } catch (Exception e) {
	        return false;
	    }
	}

	public boolean canDecrementQuantity(int index) {
	    try {
	        List<WebElement> decrementButtons = driver.findElements(
	            By.cssSelector("button[data-a-selector='decrement']"));
	        
	        if (index < 0 || index >= decrementButtons.size()) {
	            return false;
	        }
	        
	        WebElement button = decrementButtons.get(index);
	        return button.isEnabled() && !button.getAttribute("class").contains("disabled");
	    } catch (Exception e) {
	        return false;
	    }
	}
}
