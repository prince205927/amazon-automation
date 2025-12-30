package com.amazon.automation.components;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.amazon.automation.base.BaseComponent;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

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

	private By priceRanges = By.xpath("//span[@data-action='sf-select-dynamic-filter']//a");

	public WebElement findPriceRangeByIndex(int index) {
        LoggerUtil.info("Finding price range by index: " + index);
		List<WebElement> ranges = wait.visibleAll(priceRanges);
		return ranges.get(index);
	}

	public void clickPriceRangeByIndex(int index) {

		   LoggerUtil.info("Clicking price range by index: " + index);
		        ExtentReportLogger.logStep("Selecting price range by index: " + index);
		findPriceRangeByIndex(index).click();
		wait.urlContains("p_36");

        ExtentReportLogger.pass("Price range filter applied");

	}

	// brand filters
	public WebElement findBrand(String brand) {

        LoggerUtil.info("Locating brand filter: " + brand);

		By primaryLocator = brandPrimaryByName(brand);
		By fallbackLocator = brandFallbackSeeMore();

		try {
			return wait.clickable(primaryLocator);

		} catch (TimeoutException e) {

LoggerUtil.warn("Primary brand locator failed for: " + brand + ". Trying fallback.");

            ExtentReportLogger.info("Expanding brand list via 'See more'");
			wait.clickable(fallbackLocator).click();

            LoggerUtil.info("Retrying primary locator for brand: " + brand);
			return wait.clickable(primaryLocator);
		}
	}

	public void clickBrand(String brand) {

		 LoggerUtil.info("Clicking brand filter: " + brand);
		        ExtentReportLogger.logStep("Selecting brand: " + brand);

		findBrand(brand).click();
		wait.urlContains("p_123");

        ExtentReportLogger.pass("Brand filter applied: " + brand);

	}

	public boolean selectedStateCheck(String brand) {
		return Boolean.parseBoolean(findBrand(brand).getAttribute("aria-current"));
	}

	// ratings filters
	public WebElement findRatings(String state) {

        LoggerUtil.info("Locating ratings filter state: " + state);

		if ("before".equalsIgnoreCase(state)) {
			return wait.clickable(ratingBefore);
		} else {
			return wait.clickable(ratingAfter);
		}
	}

	public void clickRatings() {

		   LoggerUtil.info("Clicking ratings filter");
		        ExtentReportLogger.logStep("Applying ratings filter");

		findRatings("before").click();
	}

	public boolean selectedStateCheck() {
		return Boolean.parseBoolean(findRatings("after").getAttribute("aria-current"));
	}

	// price filter links
	public WebElement findPriceRange(String range) {

        LoggerUtil.info("Locating price range by text: " + range);

		return wait.clickable(priceRangeByText(range));
	}

	public void clickPriceRange(String range) {

		   LoggerUtil.info("Clicking price range: " + range);
		        ExtentReportLogger.logStep("Selecting price range: " + range);

		findPriceRange(range).click();
		wait.urlContains("p_36");
	}

	// price slider logic
	private Map<Integer, Integer> priceToPosMap = null;

	private void buildPriceMapping() {
		if (priceToPosMap != null) {

            LoggerUtil.info("Price-to-position mapping already built");

			return;
		}

LoggerUtil.info("Building price-to-position mapping for slider");
        ExtentReportLogger.info("Initializing price slider mapping");

		WebElement slider = driver.findElement(lowerPriceSlider);

		int min = Integer.parseInt(slider.getAttribute("min"));
		int max = Integer.parseInt(slider.getAttribute("max"));

		priceToPosMap = new HashMap<>();

		// sampling every position to build complete mapping
		for (int pos = min; pos <= max; pos++) {

			jsSetValueWithInput(slider, pos);

			String priceText = slider.getAttribute("aria-valuetext");
			int price = Integer.parseInt(priceText.replaceAll("[^0-9]", ""));
			priceToPosMap.put(price, pos);
		}
	    LoggerUtil.info("Price mapping built successfully. Total entries: " + priceToPosMap.size());
	}

	public void setPriceRangeBySlider(int minPrice, int maxPrice) {

LoggerUtil.info("Setting price range via slider: " + minPrice + " - " + maxPrice);
        ExtentReportLogger.logStep(
                "Adjusting price slider to range ₹" + minPrice + " - ₹" + maxPrice);

		buildPriceMapping();
		WebElement lowerSlider = driver.findElement(lowerPriceSlider);
		WebElement upperSlider = driver.findElement(upperPriceSlider);

		int lowerPos = findClosestPosition(minPrice);
		int upperPos = findClosestPosition(maxPrice);

		jsSetValueWithInputAndChange(lowerSlider, lowerPos);
		jsSetValueWithInputAndChange(upperSlider, upperPos);

        ExtentReportLogger.pass("Price range set via slider successfully");


	}

	private int findClosestPosition(int targetPrice) {
		return priceToPosMap.entrySet().stream().min(Comparator.comparingInt(e -> Math.abs(e.getKey() - targetPrice)))
				.map(Map.Entry::getValue).orElse(0);
	}
}
