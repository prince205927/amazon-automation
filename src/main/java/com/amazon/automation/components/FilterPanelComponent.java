package com.amazon.automation.components;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.amazon.automation.base.BaseComponent;

public class FilterPanelComponent extends BaseComponent {

	private final By ratingBefore = By.cssSelector("a[aria-label='Apply 4 Stars & Up filter to narrow results']");

	private final By ratingAfter = By.cssSelector("a[aria-label='Remove 4 Stars & Up filter to expand results']");

	private final By lowerPriceSlider = By.id("p_36/range-slider_slider-item_lower-bound-slider");

	private final By upperPriceSlider = By.id("p_36/range-slider_slider-item_upper-bound-slider");

	public FilterPanelComponent(WebDriver driver) {
		super(driver);
	}

	private By brandPrimaryByName(String brand) {
		return By.xpath("//ul[@id='filter-p_123']//a[normalize-space()='" + brand + "']");
	}

	private By brandFallbackSeeMore() {
		return By.xpath("//ul[@id='filter-p_123'] //a[normalize-space()='See more']");
	}

	private By priceRangeByText(String range) {
		return By.xpath("//span[@data-action='sf-select-dynamic-filter']//a[normalize-space()='" + range + "']");
	}

	// brand filters
	public WebElement findBrand(String brand) {
		By primaryLocator = brandPrimaryByName(brand);
		By fallbackLocator = brandFallbackSeeMore();

		try {
			return wait.clickable(primaryLocator);

		} catch (TimeoutException e) {
			System.out.println("Primary xpath failed. Trying fallback...");
			wait.clickable(fallbackLocator).click();
			System.out.println("Fallback succeeded. Retrying primary xpath...");
			return wait.clickable(primaryLocator);
		}
	}

	public void clickBrand(String brand) {
		findBrand(brand).click();
		wait.urlContains("p_123");
	}

	public boolean selectedStateCheck(String brand) {
		return Boolean.parseBoolean(findBrand(brand).getAttribute("aria-current"));
	}

	// ratings filters
	public WebElement findRatings(String state) {
		if ("before".equalsIgnoreCase(state)) {
			return wait.clickable(ratingBefore);
		} else {
			return wait.clickable(ratingAfter);
		}
	}

	public void clickRatings() {
		findRatings("before").click();
	}

	public boolean selectedStateCheck() {
		return Boolean.parseBoolean(findRatings("after").getAttribute("aria-current"));
	}

	// price filter links
	public WebElement findPriceRange(String range) {
		return wait.clickable(priceRangeByText(range));
	}

	public void clickPriceRange(String range) {
		findPriceRange(range).click();
		wait.urlContains("p_36");
	}

	// price slider logic
	private Map<Integer, Integer> priceToPosMap = null;

	private void buildPriceMapping() {
		if (priceToPosMap != null)
			return;

		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement slider = driver.findElement(lowerPriceSlider);

		int min = Integer.parseInt(slider.getAttribute("min"));
		int max = Integer.parseInt(slider.getAttribute("max"));

		priceToPosMap = new HashMap<>();

		// sampling every position to build complete mapping
		for (int pos = min; pos <= max; pos++) {
			js.executeScript(
					"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles: true}));",
					slider, pos);

			String priceText = slider.getAttribute("aria-valuetext");
			int price = Integer.parseInt(priceText.replaceAll("[^0-9]", ""));
			priceToPosMap.put(price, pos);
		}
	}

	public void setPriceRangeBySlider(int minPrice, int maxPrice) {
		buildPriceMapping();

		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement lowerSlider = driver.findElement(lowerPriceSlider);
		WebElement upperSlider = driver.findElement(upperPriceSlider);

		int lowerPos = findClosestPosition(minPrice);
		int upperPos = findClosestPosition(maxPrice);

		js.executeScript(
				"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles: true})); arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
				lowerSlider, lowerPos);

		js.executeScript(
				"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles: true})); arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
				upperSlider, upperPos);
	}

	private int findClosestPosition(int targetPrice) {
		return priceToPosMap.entrySet().stream().min(Comparator.comparingInt(e -> Math.abs(e.getKey() - targetPrice)))
				.map(Map.Entry::getValue).orElse(0);
	}
}
