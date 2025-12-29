package com.amazon.automation.pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
		wait.visible(productContainer);
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
		return sizes;
	}

	public List<VariationCombination> getAllVariationCombinations() {
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
		return combinations;
	}

	public VariationCombination selectRandomVariation() {
		List<VariationCombination> combinations = getAllVariationCombinations();
		if (combinations.isEmpty())
			return new VariationCombination(null, null);

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
			return false;
		}
	}

	public ProductDetailsPage selectVariation(String color, String size) {
		if (color != null && !color.isEmpty())
			chooseColor(color);
		if (size != null && !size.isEmpty())
			chooseSize(size);
		return this;
	}

	public String chooseColor(String text) {
		By colorsLocator = By.cssSelector("span.image-swatch-button-with-slots img.swatch-image");
		By selectedColorTextLocator = By.cssSelector("span#inline-twister-expanded-dimension-text-color_name");
		List<WebElement> colors = driver.findElements(colorsLocator);
		for (WebElement colorImg : colors) {
			String altText = colorImg.getAttribute("alt").trim();
			if (text.equalsIgnoreCase(altText)) {
				WebElement clickable = colorImg
						.findElement(By.xpath("./ancestor::span[contains(@class,'image-swatch-button-with-slots')]"));
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", clickable);
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
				wait.presenceInElement(selectedColorTextLocator, text);
				return altText;
			}
		}
		return null;
	}

	public String chooseSize(String text) {
		By sizesLocator = By.cssSelector("div#inline-twister-row-size_name span.swatch-title-text-display");
		List<WebElement> sizes = driver.findElements(sizesLocator);
		Actions actions = new Actions(driver);
		By selectedSizeTextLocator = By.cssSelector("span#inline-twister-expanded-dimension-text-size_name");

		for (WebElement size : sizes) {
			String sizeText = size.getAttribute("innerText").trim();
			if (text.equalsIgnoreCase(sizeText)) {
				WebElement clickable = size
						.findElement(By.xpath("./ancestor::div[contains(@class, 'swatch-title-text-container')]"));
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", clickable);
				try {
					actions.moveToElement(clickable).click().perform();
				} catch (Exception e) {
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickable);
				}
				wait.presenceInElement(selectedSizeTextLocator, text);
				return sizeText;
			}
		}
		return null;
	}

	public ProductDetailsPage selectQuantity(String quantity) {
		try {
			WebElement dropdown = wait.presenceOfElement(quantitySelect);
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dropdown);
			Select select = new Select(dropdown);
			try {
				select.selectByValue(quantity);
			} catch (Exception e) {
				select.selectByVisibleText(quantity);
			}
		} catch (Exception e) {
			try {
				WebElement dropdown = driver.findElement(quantitySelect);
				((JavascriptExecutor) driver).executeScript(
						"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));", dropdown,
						quantity);
			} catch (Exception ex) {
			}
		}
		return this;
	}

	public Integer getQuantity() {
		try {
			return Integer.parseInt(
					new Select(wait.presenceOfElement(quantitySelect)).getFirstSelectedOption().getText().trim());
		} catch (Exception e) {
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
		wait.clickable(wait.presenceOfElement(fullViewLink)).click();
		waitForTransformToComplete();
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
		ProductData product = new ProductData();
		product.setTitle(getProductName());
		product.setPrice(variations.getPrice());
		product.setQuantity(getQuantity());
		product.setSize(variations.getSelectedSize());
		product.setColor(variations.getSelectedColor());
		product.setSource("PDP");
		return product;
	}

	public String getProductName() {
		return wait.presenceOfElement(productTitle).getAttribute("innerText").trim();
	}

	public ModalComponent goToModal() {
		return new ModalComponent(driver);
	}

	public CartPage addToCart() {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", wait.presenceOfElement(addToCartButton));
		wait.visible(By.xpath("//h1[contains(.,'Added to cart')]"));
		return new CartPage(driver);
	}

	public boolean isOutOfStockMessageVisible() {
		return wait.visible(outOfStockDiv).isDisplayed();
	}

	public boolean isAddToCartPresent() {
		try {
			return wait.presenceOfElement(addToCartButton).isDisplayed();
		} catch (TimeoutException e) {
			return false;
		}
	}

	public boolean isAddToCartAvailable() {
		try {
			WebElement button = driver.findElement(addToCartButton);
			return button.isDisplayed() && button.isEnabled() && !button.getAttribute("class").contains("disabled");
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	public boolean isQuantitySelectorAvailable() {
		try {
			WebElement dropdown = driver.findElement(quantitySelect);
			return dropdown.isDisplayed() && dropdown.isEnabled();
		} catch (NoSuchElementException e) {
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
		List<VariationCombination> allCombinations = getAllVariationCombinations();
		for (VariationCombination combo : allCombinations) {
			if (combo.getColor() != null)
				chooseColor(combo.getColor());
			if (combo.getSize() != null)
				chooseSize(combo.getSize());
			if (hasOutOfStockMessage() && !isVariationAvailable())
				return combo;
		}
		return null;
	}

	public ProductVariationsComponent goToVariations() {
		return new ProductVariationsComponent(driver);
	}

	public List<VariationCombination> getAllOutOfStockVariations() {
		List<VariationCombination> outOfStockVariations = new ArrayList<>();
		for (VariationCombination combo : getAllVariationCombinations()) {
			if (combo.getColor() != null)
				chooseColor(combo.getColor());
			if (combo.getSize() != null)
				chooseSize(combo.getSize());
			if (!isVariationAvailable())
				outOfStockVariations.add(combo);
		}
		return outOfStockVariations;
	}
}