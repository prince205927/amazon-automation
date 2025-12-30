package com.amazon.automation.pages;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.amazon.automation.base.BasePage;
import com.amazon.automation.tests.models.ProductData;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;
import com.amazon.automation.utils.WaitUtils;

public class SearchResultsPage extends BasePage {

	private final By searchTermHeader = By.cssSelector("span.a-color-state");
	private final By mainSlot = By.cssSelector("div.s-main-slot");
	private final By resultTiles = By.cssSelector("div.s-main-slot div[data-component-type='s-search-resul']");
	private final By productTitleInTile = By.cssSelector("h2.a-size-medium span");
	private final By ratingsInTile = By.cssSelector("a.a-popover-trigger");
	private final By priceWholeInTile = By.cssSelector("span.a-price-whole");
	private final By spinner = By.cssSelector("div.s-loading-spinner");
	private final By pagination = By.cssSelector("div[aria-label='paginatio']");
	private final By nextPageButton = By
			.xpath("//a[contains(@class,'s-pagination-next') and not(contains(@class,'s-pagination-disabled'))]");

	public SearchResultsPage(WebDriver driver) {
		super(driver);
	}

	public SearchResultsPage waitForResults() {

		LoggerUtil.info("Waiting for search results page to load");

		wait.visible(mainSlot);

		ExtentReportLogger.info("Search results loaded");

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

		LoggerUtil.info("Validating product titles contain brand: " + brand);

		String requestedBrand = brand.trim().toLowerCase();
		List<WebElement> productTitles = driver.findElements(
				By.cssSelector("div.s-main-slot div[data-asin][data-component-type='s-search-result'] h2 a span"));
		for (WebElement title : productTitles) {
			String obtainedBrand = title.getText().trim().toLowerCase();
			if (!obtainedBrand.contains(requestedBrand)) {

				LoggerUtil.warn("Brand mismatch in title: " + title.getText());
				return false;
			}
		}
		return true;
	}

	public boolean checkRatings() {

		LoggerUtil.info("Verifying product ratings >= 4.0");

		List<WebElement> ratings = driver
				.findElements(By.cssSelector("div.s-main-slot div[data-asin] a.a-popover-trigger"));
		for (WebElement rating : ratings) {
			String ratingText = rating.getAttribute("aria-label");
			double ratingValue = Double.parseDouble(ratingText.split(" ")[0].replace(",", "."));
			if (ratingValue < 4.0) {

				LoggerUtil.warn("Rating validation failed: " + ratingValue);
				return false;
			}
		}
		return true;
	}

	public List<Integer> getAllPrices() {

		LoggerUtil.info("Collecting all visible prices from search results");

		List<Integer> prices = new ArrayList<>();
		List<WebElement> priceElements = driver.findElements(priceWholeInTile);
		for (WebElement el : priceElements) {
			String text = el.getText().replace(",", "").trim();
			if (!text.isEmpty())
				prices.add(Integer.parseInt(text));
		}

		LoggerUtil.info("Prices collected: " + prices.size());

		return prices;
	}

	public SearchResultsPage waitForSpinnerToDisappear() {
		wait.invisible(spinner);

		LoggerUtil.info("Listing spinner disappeared");

		return this;
	}

	public void scrollToPagination() {
		WebElement paginationElement = driver.findElement(pagination);

		LoggerUtil.info("Scrolling to pagination section");

		jsScrollIntoViewTop(paginationElement);
	}

	public void goToNextPage() {

		LoggerUtil.info("Navigating to next results page");
		ExtentReportLogger.logStep("Navigating to next search results page");

		WebElement nextButton = driver.findElement(nextPageButton);
		jsClick(nextButton);

		ExtentReportLogger.pass("Next page opened");

	}

	public void openProductByIndex(int index) {

		LoggerUtil.info("Opening product from search results at index: " + index);
		ExtentReportLogger.logStep("Opening product from listing (index " + index + ")");

		wait.visible(mainSlot);
		List<WebElement> tiles = driver
				.findElements(By.xpath("//div[@data-asin and @data-component-type='s-search-result']//h2//span"));
		if (tiles.size() < index)
			throw new IllegalArgumentException("Not enough results. Found: " + tiles.size() + ", Requested: " + index);
		WebElement tile = tiles.get(index - 1);
		jsScrollIntoViewCenter(tile);
		wait.clickable(tile);
		jsClick(tile);
		wait.visible(By.id("productTitle"));

		ExtentReportLogger.pass("Product details page opened");

	}

	public void addToCartFromListingByIndex(int index) {

		LoggerUtil.info("Attempting to add product to cart from listing, index: " + index);
		ExtentReportLogger.logStep("Adding product to cart from search listing");

		try {
			List<WebElement> tiles = driver.findElements(resultTiles);
			if (index < 0 || index >= tiles.size()) {
				throw new IndexOutOfBoundsException("Invalid index: " + index);
			}

			WebElement tile = tiles.get(index);
			String asin = tile.getAttribute("data-asin");

			// Find Add to Cart button within this tile
			WebElement addToCartButton = tile.findElement(By.cssSelector("button[aria-label*='Add to cart']"));

			// Scroll into view
			jsScrollIntoViewCenter(addToCartButton);

			System.out.println("Clicking Add to Cart for ASIN: " + asin);
			jsClick(addToCartButton);
			waitForListingSpinnerToDisappear();

		} catch (Exception e) {

			LoggerUtil.warn("Error adding product to cart at index " + index);
			ExtentReportLogger.logStep("Failed to add product to cart from listing");
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

			LoggerUtil.info("Listing spinner disappeared");
		} catch (Exception e) {

			LoggerUtil.info("Spinner wait completed (fallback reached)");
		}
	}

	public ProductData getProductDataFromListingByIndex(int index) {

		LoggerUtil.info("Capturing product data from listing index: " + index);

		ProductData product = new ProductData();
		try {
			List<WebElement> tiles = driver.findElements(resultTiles);
			if (index < 0 || index >= tiles.size()) {
				throw new IndexOutOfBoundsException("Invalid index: " + index + ", total tiles: " + tiles.size());
			}

			WebElement tile = tiles.get(index);

			// Get ASIN
			String asin = tile.getAttribute("data-asin");
			product.setAsin(asin);

			// Get title
			try {
				WebElement titleElement = tile.findElement(By.cssSelector("h2 span"));
				product.setTitle(titleElement.getText().trim());
			} catch (NoSuchElementException e) {
				System.out.println("Could not find title for index " + index);
				product.setTitle("Unknown Title");
			}

			// Get price with multiple fallbacks
			double price = getPriceFromTile(tile, asin);
			product.setPrice(price);

			product.setQuantity(1);
			product.setSource("Listing");

			LoggerUtil.info("Captured listing product: " + asin + " | $" + price);

		} catch (Exception e) {

			LoggerUtil.warn("Error capturing product data from listing at index " + index);
		}
		return product;
	}

	private double getPriceFromTile(WebElement tile, String asin) {
		// Strategy 1: Look for offscreen price (most reliable)
		try {
			WebElement priceElement = tile.findElement(By.cssSelector("span.a-price span.a-offscreen"));
			return parsePrice(priceElement.getText());
		} catch (Exception e1) {

			LoggerUtil.info("Price strategy 1 failed for ASIN: " + asin);
		}

		// Strategy 2: Look for visible whole + fraction
		try {
			WebElement wholeElement = tile.findElement(By.cssSelector("span.a-price-whole"));
			String wholeText = wholeElement.getText().replace(",", "").trim();

			try {
				WebElement fractionElement = tile.findElement(By.cssSelector("span.a-price-fraction"));
				String fractionText = fractionElement.getText().trim();
				return Double.parseDouble(wholeText + "." + fractionText);
			} catch (Exception e) {
				// No fraction, just use whole number
				return Double.parseDouble(wholeText);
			}
		} catch (Exception e2) {

			LoggerUtil.info("Price strategy 2 failed for ASIN: " + asin);
		}

		// Strategy 3: Look in secondary offer recipe
		try {
			WebElement secondaryPrice = tile
					.findElement(By.cssSelector("div[data-cy='secondary-offer-recipe'] span.a-color-base"));
			return parsePrice(secondaryPrice.getText());
		} catch (Exception e3) {

			LoggerUtil.warn("All price strategies failed for ASIN: " + asin);
		}

		System.out.println(" Could not extract price for ASIN " + asin + " - returning 0.0");
		return 0.0;
	}

	public double getPriceForProduct(String asin) {

		LoggerUtil.info("Fetching price for ASIN: " + asin);

		String primarySelector = "div[data-asin='" + asin + "'] span.a-price:not(.a-text-price) span.a-offscreen";
		String fallbackSelector = "div[data-asin='" + asin
				+ "'] div[data-cy='secondary-offer-recipe'] span.a-color-base";
		try {
			return parsePrice(driver.findElement(By.cssSelector(primarySelector)).getText());
		} catch (Exception e1) {
			try {
				return parsePrice(driver.findElement(By.cssSelector(fallbackSelector)).getText());
			} catch (Exception e2) {

				LoggerUtil.warn("Price not found for ASIN: " + asin);
				return 0.0;
			}
		}
	}

	private double parsePrice(String text) {
		return Double.parseDouble(text.replace("$", "").replace(",", "").trim());
	}

	public boolean hasAddToCartButtonByIndex(int index) {

		LoggerUtil.info("Checking Add to Cart button presence for index: " + index);

		try {
			List<WebElement> tiles = driver.findElements(resultTiles);
			if (index < 0 || index >= tiles.size()) {

				LoggerUtil.warn("Invalid index while checking Add to Cart: " + index);

				return false;
			}

			WebElement tile = tiles.get(index);
			List<WebElement> buttons = tile.findElements(By.cssSelector("button[aria-label*='Add to cart']"));

			return !buttons.isEmpty() && buttons.get(0).isDisplayed();
		} catch (Exception e) {

			LoggerUtil.warn("Error checking Add to Cart button at index " + index);
			return false;
		}
	}
}