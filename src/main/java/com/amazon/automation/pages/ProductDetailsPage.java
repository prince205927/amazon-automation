package com.amazon.automation.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
import com.amazon.automation.tests.models.ProductData;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

public class ProductDetailsPage extends BasePage {

	private final ProductVariationsComponent variations;

	private final By productContainer = By.id("ppd");
	private final By productTitle = By.id("productTitle");
	private final By landingImage = By.id("landingImage");
	private final By imageThumbnails = By.cssSelector("li.imageThumbnail img");
	private final By ratings = By.cssSelector("a.a-popover-trigger span.a-icon-alt");
	private final By availabilityText = By.cssSelector("div#availability span.a-color-success");
	private final By fullViewLink = By.partialLinkText("Click to see full view");
	private final By addToCartButton = By.cssSelector("input#add-to-cart-button");
	private final By outOfStockDiv = By.cssSelector("div#outOfStock");
	private final By quantitySelect = By.cssSelector("select#quantity");

	public ProductDetailsPage(WebDriver driver) {
		super(driver);
		this.variations = new ProductVariationsComponent(driver);
	}

	public ProductVariationsComponent variations() {
		return variations;
	}

	public ProductDetailsPage waitForResults() {

		LoggerUtil.info("Waiting for Product Details Page to load");

		wait.visible(productContainer);

		ExtentReportLogger.info("Product details section loaded");

		return this;
	}

	public static class VariationCombination {
		private String color;
		private String size;

		public VariationCombination(String color, String size) {
			this.color = color;
			this.size = size;
		}

		public String getColor() {
			return color;
		}

		public String getSize() {
			return size;
		}

		@Override
		public String toString() {
			return "Color: " + color + ", Size: " + size;
		}
	}

	public List<String> getAvailableColors() {
		List<String> colors = new ArrayList<>();
		List<WebElement> colorElements = driver
				.findElements(By.cssSelector("span.image-swatch-button-with-slots img.swatch-image"));
		for (WebElement colorImg : colorElements) {
			String altText = colorImg.getAttribute("alt");
			if (altText != null && !altText.trim().isEmpty())
				colors.add(altText.trim());
		}

		LoggerUtil.info("Available colors found: " + colors);

		return colors;
	}

	public List<String> getAvailableSizes() {
		List<String> sizes = new ArrayList<>();
		List<WebElement> sizeElements = driver
				.findElements(By.cssSelector("div#inline-twister-row-size_name span.swatch-title-text-display"));
		for (WebElement size : sizeElements) {
			String sizeText = size.getAttribute("innerText");
			if (sizeText != null && !sizeText.trim().isEmpty())
				sizes.add(sizeText.trim());
		}

		LoggerUtil.info("Available sizes found: " + sizes);

		return sizes;
	}

	public List<VariationCombination> getAllVariationCombinations() {

		LoggerUtil.info("Building all variation combinations");

		List<VariationCombination> combinations = new ArrayList<>();
		List<String> colors = getAvailableColors();
		List<String> sizes = getAvailableSizes();

		if (!colors.isEmpty() && !sizes.isEmpty()) {
			for (String color : colors)
				for (String size : sizes)
					combinations.add(new VariationCombination(color, size));
		} else if (!colors.isEmpty()) {
			for (String color : colors)
				combinations.add(new VariationCombination(color, null));
		} else if (!sizes.isEmpty()) {
			for (String size : sizes)
				combinations.add(new VariationCombination(null, size));
		}

		LoggerUtil.info("Total variation combinations generated: " + combinations.size());

		return combinations;
	}

	public VariationCombination selectRandomVariation() {

LoggerUtil.info("Selecting random valid variation");
    ExtentReportLogger.logStep("Selecting random product variation");

		List<VariationCombination> combinations = getAllVariationCombinations();
		if (combinations.isEmpty()) {

	        LoggerUtil.warn("No variation combinations available");

			return new VariationCombination(null, null);
		}

		Random random = new Random();
		List<VariationCombination> attempted = new ArrayList<>();
		int maxAttempts = Math.min(combinations.size(), 5);

		for (int i = 0; i < maxAttempts; i++) {
			VariationCombination combo;
			do {
				combo = combinations.get(random.nextInt(combinations.size()));
			} while (attempted.contains(combo) && attempted.size() < combinations.size());

			attempted.add(combo);

			if (combo.getColor() != null)
				chooseColor(combo.getColor());
			if (combo.getSize() != null)
				chooseSize(combo.getSize());

			if (!hasOutOfStockMessage() && isVariationAvailable())
				return combo;
		}

LoggerUtil.warn("No valid variation found after attempts");
    ExtentReportLogger.logStep("No valid variation available");

		return null;
	}

	private boolean isVariationAvailable() {
		try {
			WebElement button = driver.findElement(addToCartButton);
			boolean addToCartAvailable = button.isDisplayed() && button.isEnabled()
					&& !button.getAttribute("class").contains("disabled");
			if (!addToCartAvailable)
				return false;

			List<WebElement> qtyDropdowns = driver.findElements(quantitySelect);
			if (!qtyDropdowns.isEmpty()) {
				WebElement qty = qtyDropdowns.get(0);
				return qty.isDisplayed() && qty.isEnabled();
			}
			return true;
		} catch (Exception e) {

			LoggerUtil.warn("Variation availability check failed");

			return false;
		}
	}

	public ProductDetailsPage selectVariation(String color, String size) {

		LoggerUtil.info("Selecting variation - Color: " + color + ", Size: " + size);
		ExtentReportLogger.logStep("Selecting product variation");

		if (color != null && !color.isEmpty())
			chooseColor(color);
		if (size != null && !size.isEmpty())
			chooseSize(size);

		ExtentReportLogger.pass("Variation applied successfully");

		return this;
	}

	public String chooseColor(String text) {

		LoggerUtil.info("Choosing color: " + text);
		ExtentReportLogger.logStep("Selecting color: " + text);

		By colorsLocator = By.cssSelector("span.image-swatch-button-with-slots img.swatch-image");
		By selectedColorTextLocator = By.cssSelector("span#inline-twister-expanded-dimension-text-color_name");
		List<WebElement> colors = driver.findElements(colorsLocator);
		for (WebElement colorImg : colors) {
			String altText = colorImg.getAttribute("alt").trim();
			if (text.equalsIgnoreCase(altText)) {
				WebElement clickable = colorImg
						.findElement(By.xpath("./ancestor::span[contains(@class,'image-swatch-button-with-slots')]"));
				jsScrollAndClick(clickable);
				wait.presenceInElement(selectedColorTextLocator, text);

				ExtentReportLogger.pass("Color selected: " + altText);

				return altText;
			}
		}

		LoggerUtil.warn("Requested color not available: " + text);
		ExtentReportLogger.logStep("Color not available: " + text);

		return null;
	}

	public String chooseSize(String text) {

		LoggerUtil.info("Choosing size: " + text);
		ExtentReportLogger.logStep("Selecting size: " + text);

		By sizesLocator = By.cssSelector("div#inline-twister-row-size_name span.swatch-title-text-display");
		List<WebElement> sizes = driver.findElements(sizesLocator);
		Actions actions = new Actions(driver);
		By selectedSizeTextLocator = By.cssSelector("span#inline-twister-expanded-dimension-text-size_name");

		for (WebElement size : sizes) {
			String sizeText = size.getAttribute("innerText").trim();
			if (text.equalsIgnoreCase(sizeText)) {
				WebElement clickable = size
						.findElement(By.xpath("./ancestor::div[contains(@class, 'swatch-title-text-container')]"));
				jsScrollIntoViewTop(clickable);
				try {
					actions.moveToElement(clickable).click().perform();
				} catch (Exception e) {
					jsClick(clickable);
				}
				wait.presenceInElement(selectedSizeTextLocator, text);

				ExtentReportLogger.pass("Size selected: " + sizeText);

				return sizeText;
			}
		}

		LoggerUtil.warn("Requested size not available: " + text);
		ExtentReportLogger.logStep("Size not available: " + text);

		return null;
	}

	public ProductDetailsPage selectQuantity(String quantity) {

		LoggerUtil.info("Selecting quantity: " + quantity);
		ExtentReportLogger.logStep("Selecting quantity: " + quantity);

		try {
			WebElement dropdown = wait.presenceOfElement(quantitySelect);
			jsScrollIntoViewTop(dropdown);
			Select select = new Select(dropdown);
			try {
				select.selectByValue(quantity);
			} catch (Exception e) {
				select.selectByVisibleText(quantity);
			}

			ExtentReportLogger.pass("Quantity selected: " + quantity);

		} catch (Exception e) {
			try {
				WebElement dropdown = driver.findElement(quantitySelect);
				jsSetValueWithChange(dropdown, quantity);

				ExtentReportLogger.pass("Quantity set via JS: " + quantity);

			} catch (Exception ex) {

				LoggerUtil.warn("Failed to set quantity: " + quantity);
				ExtentReportLogger.logStep("Unable to set quantity");

			}
		}
		return this;
	}

	public Integer getQuantity() {
		try {
			return Integer.parseInt(
					new Select(wait.presenceOfElement(quantitySelect)).getFirstSelectedOption().getText().trim());
		} catch (Exception e) {

			LoggerUtil.info("Quantity dropdown not found, defaulting to 1");

			return 1;
		}
	}

	public String getSelectedColor() {
		try {
			return wait.presenceOfElement(By.cssSelector("span#inline-twister-expanded-dimension-text-color_name"))
					.getText().trim();
		} catch (Exception e) {

			return null;
		}
	}

	public String getSelectedSize() {
		try {
			return wait.presenceOfElement(By.cssSelector("span#inline-twister-expanded-dimension-text-size_name"))
					.getText().trim();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean hasTitle() {
		return !driver.findElement(productTitle).getText().trim().isEmpty();
	}

	public boolean hasImages() {
		WebElement mainImage = driver.findElement(landingImage);
		List<WebElement> images = driver.findElements(imageThumbnails);
		return mainImage.isDisplayed() && images.get(0).getAttribute("src") != null;
	}

	public boolean hasValidRatings() {
		List<WebElement> ratingElements = driver.findElements(ratings);
		if (ratingElements.isEmpty())
			return false;
		String text = ratingElements.get(0).getAttribute("innerText").trim().toLowerCase();
		return text.contains("out of 5");
	}

	public boolean hasAvailabilityText() {
		return !wait.presenceOfElement(availabilityText).getAttribute("innerText").trim().isEmpty();
	}

	public void clickToSeeFullView() {

		LoggerUtil.info("Opening full image view");
		ExtentReportLogger.logStep("Opening full image view");

		wait.clickable(wait.presenceOfElement(fullViewLink)).click();
		waitForTransformToComplete();

		ExtentReportLogger.pass("Full image view opened");

	}

	public void waitForTransformToComplete() {
		By transformPopover = By
				.cssSelector("div.a-popover.a-popover-modal.a-declarative.a-popover-modal-fixed-height");
		wait.visible(transformPopover);
		wait.waitUntil(driver -> {
			try {
				return !driver.findElement(transformPopover).getCssValue("transform").equals("none");
			} catch (Exception e) {
				return false;
			}
		});
		wait.presenceOfElement(By.className("fullscreen"));
	}

	public ProductData captureProductDetails() {

		LoggerUtil.info("Capturing product details from PDP");
		ExtentReportLogger.logStep("Capturing product details");

		ProductData product = new ProductData();
		product.setTitle(getProductName());
		product.setPrice(variations.getPrice());
		product.setQuantity(getQuantity());
		product.setSize(variations.getSelectedSize());
		product.setColor(variations.getSelectedColor());
		product.setSource("PDP");

		ExtentReportLogger.pass("Product details captured");

		return product;
	}

	public String getProductName() {
		return wait.presenceOfElement(productTitle).getAttribute("innerText").trim();
	}

	public ModalComponent goToModal() {

		LoggerUtil.info("Navigating to image modal component");
		ExtentReportLogger.logStep("Opening product image modal");

		return new ModalComponent(driver);
	}

	public CartPage addToCart() {

		LoggerUtil.info("Clicking Add to Cart button");
		ExtentReportLogger.logStep("Adding product to cart");

		jsClickWhenPresent(addToCartButton);
		wait.visible(By.xpath("//h1[contains(.,'Added to cart')]"));

		LoggerUtil.info("Product added to cart successfully");
		ExtentReportLogger.pass("Product added to cart");

		return new CartPage(driver);
	}

	public boolean isOutOfStockMessageVisible() {
		return wait.visible(outOfStockDiv).isDisplayed();
	}

	public boolean isAddToCartPresent() {
		try {
			return wait.presenceOfElement(addToCartButton).isDisplayed();
		} catch (TimeoutException e) {

			LoggerUtil.info("Add to Cart button not present");

			return false;
		}
	}

	public boolean isAddToCartAvailable() {
		try {
			WebElement button = driver.findElement(addToCartButton);
			return button.isDisplayed() && button.isEnabled() && !button.getAttribute("class").contains("disabled");
		} catch (NoSuchElementException e) {

			LoggerUtil.info("Add to Cart button not found");

			return false;
		}
	}

	public boolean isQuantitySelectorAvailable() {
		try {
			WebElement dropdown = driver.findElement(quantitySelect);
			return dropdown.isDisplayed() && dropdown.isEnabled();
		} catch (NoSuchElementException e) {

			LoggerUtil.info("Quantity selector not found");

			return false;
		}
	}

	public boolean hasOutOfStockMessage() {
		try {
			return wait.visible(outOfStockDiv).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public VariationCombination selectOutOfStockVariation() {

		LoggerUtil.info("Selecting out-of-stock variation");
		ExtentReportLogger.logStep("Searching for out-of-stock variation");

		List<VariationCombination> allCombinations = getAllVariationCombinations();
		for (VariationCombination combo : allCombinations) {
			if (combo.getColor() != null)
				chooseColor(combo.getColor());
			if (combo.getSize() != null)
				chooseSize(combo.getSize());
			if (hasOutOfStockMessage() && !isVariationAvailable())

				ExtentReportLogger.pass("Out-of-stock variation found: " + combo);

			return combo;
		}

		LoggerUtil.warn("No out-of-stock variation found");
		ExtentReportLogger.logStep("No out-of-stock variations available");

		return null;
	}

	public ProductVariationsComponent goToVariations() {
		return new ProductVariationsComponent(driver);
	}

	public List<VariationCombination> getAllOutOfStockVariations() {

		LoggerUtil.info("Collecting all out-of-stock variations");

		List<VariationCombination> outOfStockVariations = new ArrayList<>();
		for (VariationCombination combo : getAllVariationCombinations()) {
			if (combo.getColor() != null)
				chooseColor(combo.getColor());
			if (combo.getSize() != null)
				chooseSize(combo.getSize());
			if (!isVariationAvailable())
				outOfStockVariations.add(combo);
		}

		LoggerUtil.info("Total out-of-stock variations found: " + outOfStockVariations.size());

		return outOfStockVariations;
	}
}