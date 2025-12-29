package com.amazon.automation.pages;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.amazon.automation.base.BasePage;
import com.amazon.automation.tests.models.ProductData;
import com.amazon.automation.utils.WaitUtils;

public class SearchResultsPage extends BasePage {

	private final By searchTermHeader = By.cssSelector("span.a-color-state");
	private final By mainSlot = By.cssSelector("div.s-main-slot");
	private final By resultTiles = By.cssSelector("div.s-main-slot div[data-component-type='s-search-result']");
	private final By productTitleInTile = By.cssSelector("h2.a-size-medium span");
	private final By ratingsInTile = By.cssSelector("a.a-popover-trigger");
	private final By priceWholeInTile = By.cssSelector("span.a-price-whole");
	private final By spinner = By.cssSelector("div.s-loading-spinner");
	private final By pagination = By.cssSelector("div[aria-label='pagination']");
	private final By nextPageButton = By
			.xpath("//a[contains(@class,'s-pagination-next') and not(contains(@class,'s-pagination-disabled'))]");

	public SearchResultsPage(WebDriver driver) {
		super(driver);
	}

	public SearchResultsPage waitForResults() {
		wait.visible(mainSlot);
		return this;
	}

	public boolean hasResults() {
		List<WebElement> results = driver.findElements(resultTiles);
		return results != null && !results.isEmpty();
	}

	public String currentSearchTerm() {
		return driver.findElement(searchTermHeader).getText().trim();
	}

	public boolean checkProductTitles(String brand) {
		String requestedBrand = brand.trim().toLowerCase();
		List<WebElement> productTitles = driver.findElements(
				By.cssSelector("div.s-main-slot div[data-asin][data-component-type='s-search-result'] h2 a span"));
		for (WebElement title : productTitles) {
			String obtainedBrand = title.getText().trim().toLowerCase();
			if (!obtainedBrand.contains(requestedBrand)) {
				System.out.println("The unmatchable title is " + title.getText());
				return false;
			}
		}
		return true;
	}

	public boolean checkRatings() {
		List<WebElement> ratings = driver
				.findElements(By.cssSelector("div.s-main-slot div[data-asin] a.a-popover-trigger"));
		for (WebElement rating : ratings) {
			String ratingText = rating.getAttribute("aria-label");
			double ratingValue = Double.parseDouble(ratingText.split(" ")[0].replace(",", "."));
			if (ratingValue < 4.0) {
				System.out.println("Product with rating " + ratingValue + " failed the check.");
				return false;
			}
		}
		return true;
	}

	public List<Integer> getAllPrices() {
		List<Integer> prices = new ArrayList<>();
		List<WebElement> priceElements = driver.findElements(priceWholeInTile);
		for (WebElement el : priceElements) {
			String text = el.getText().replace(",", "").trim();
			if (!text.isEmpty())
				prices.add(Integer.parseInt(text));
		}
		return prices;
	}

	public SearchResultsPage waitForSpinnerToDisappear() {
		wait.invisible(spinner);
		return this;
	}

	public void scrollToPagination() {
		WebElement paginationElement = driver.findElement(pagination);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", paginationElement);
	}

	public void goToNextPage() {
		WebElement nextButton = driver.findElement(nextPageButton);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);
	}

	public void openProductByIndex(int index) {
		wait.visible(mainSlot);
		List<WebElement> tiles = driver
				.findElements(By.xpath("//div[@data-asin and @data-component-type='s-search-result']//h2//span"));
		if (tiles.size() < index)
			throw new IllegalArgumentException("Not enough results. Found: " + tiles.size() + ", Requested: " + index);
		WebElement tile = tiles.get(index - 1);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", tile);
		wait.clickable(tile);
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", tile);
		wait.visible(By.id("productTitle"));
	}

	public void addToCartFromListingByIndex(int index, String asin) {
		try {
			if (!hasAddToCartButtonByIndex(index)) {
				System.out.println("No Add to Cart button for product index: " + index + ". Skipping...");
				return;
			}
			List<WebElement> addToCartButtons = driver.findElements(By.cssSelector("button[aria-label='Add to cart']"));
			if (index < 0 || index >= addToCartButtons.size())
				throw new IndexOutOfBoundsException("Invalid product index: " + index);

			WebElement addToCartButton = addToCartButtons.get(index);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartButton);
			System.out.println("Clicking Add to Cart button for product index: " + index);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartButton);

			System.out.println("Waiting for loading animation...");
			waitForListingSpinnerToDisappear();
			waitForUpdateDivToAppear(asin);
		} catch (Exception e) {
			System.out.println("Error adding to cart from listing: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void waitForUpdateDivToAppear(String asin) {
		wait.visible(By.cssSelector("div.atc-faceout-container[data-asin='" + asin + "']"));
	}

	private void waitForListingSpinnerToDisappear() {
		try {
			By spinnerLocator = By.cssSelector("span.a-spinner.a-spinner-medium");
			wait.presenceOfElement(spinnerLocator);
			wait.invisible(spinnerLocator);
			System.out.println("✓ Spinner disappeared");
		} catch (Exception e) {
			System.out.println("Spinner wait completed: " + e.getMessage());
		}
	}

	public ProductData getProductDataFromListingByIndex(int index) {
		ProductData product = new ProductData();
		try {
			List<WebElement> titles = driver.findElements(By.cssSelector("div[data-cy='title-recipe'] h2"));
			if (index < titles.size())
				product.setTitle(titles.get(index).getAttribute("textContent").trim());
			System.out.println("Got index " + index + " and for that product title is " + product.getTitle());

			List<WebElement> tiles = driver
					.findElements(By.xpath("//div[@data-asin and @data-component-type='s-search-result']"));
			if (index < tiles.size())
				product.setAsin(tiles.get(index).getAttribute("data-asin").trim());

			String asin = product.getAsin();
			double price = getPriceForProduct(asin);
			System.out.println("\nPrice is " + price);
			product.setPrice(price);
			product.setQuantity(1);
			product.setSource("Listing");

			System.out.println("Captured product from listing: " + product.getTitle() + " | $" + product.getPrice());
		} catch (Exception e) {
			System.out.println("Error capturing product data from listing: " + e.getMessage());
		}
		return product;
	}

	public double getPriceForProduct(String asin) {
		String primarySelector = "div[data-asin='" + asin + "'] span.a-price:not(.a-text-price) span.a-offscreen";
		String fallbackSelector = "div[data-asin='" + asin
				+ "'] div[data-cy='secondary-offer-recipe'] span.a-color-base";
		try {
			return parsePrice(driver.findElement(By.cssSelector(primarySelector)).getText());
		} catch (Exception e1) {
			try {
				return parsePrice(driver.findElement(By.cssSelector(fallbackSelector)).getText());
			} catch (Exception e2) {
				System.out.println("Price not found for ASIN: " + asin + " -> " + e2.getMessage());
				return 0.0;
			}
		}
	}

	private double parsePrice(String text) {
		return Double.parseDouble(text.replace("$", "").replace(",", "").trim());
	}

	public boolean hasAddToCartButtonByIndex(int index) {
		try {
			List<WebElement> buttons = driver.findElements(By.xpath(
					"//div[@data-asin and @data-component-type='s-search-result'] //button[@aria-label='Add to cart']"));
			WebElement button = buttons.get(index);
			return button.isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}
}