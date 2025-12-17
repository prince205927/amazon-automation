package com.amazon.automation.pages;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.amazon.automation.base.BasePage;
import com.amazon.automation.utils.WaitUtils;

public class SearchResultsPage extends BasePage {

	private final WaitUtils wait;

	// search term heading
	@FindBy(css = "span.a-color-state")
	private WebElement searchTermHeader;

	// product title links
	@FindBy(css = "div[data-component-type='s-search-result']")
	private List<WebElement> titleLinks;

	// main container
	@FindBy(css = "div.s-main-slot")
	private WebElement mainSlot;

	public SearchResultsPage(WebDriver driver) {
		super(driver);
		this.wait = new WaitUtils(driver, 15);
	}

	public SearchResultsPage waitForResults() {
		wait.visible(mainSlot);
		return this;
	}

	public boolean hasResults() {
		return titleLinks != null && !titleLinks.isEmpty();
	}

	public String currentSearchTerm() {
		return searchTermHeader.getText();
	}
	
	
	
	
	
	

@FindBy(css = "div[data-component-type='s-search-result']")
    private List<WebElement> tiles;


/** Extract visible prices from tiles (USD or localized currency). */
    public List<BigDecimal> getVisiblePrices(int limit) {
        List<BigDecimal> prices = new ArrayList<>();
        int count = tiles == null ? 0 : tiles.size();
        for (int i = 0; i < Math.min(limit, count); i++) {
            try {
                WebElement t = tiles.get(i);
                // Price pieces: integer + fraction
                List<WebElement> intPartEls = t.findElements(By.cssSelector("span.a-price-whole"));
                List<WebElement> fracPartEls = t.findElements(By.cssSelector("span.a-price-fraction"));
                if (!intPartEls.isEmpty()) {
                    String intPart = intPartEls.get(0).getText().replaceAll("[^\\d]", "");
                    String fracPart = !fracPartEls.isEmpty() ? fracPartEls.get(0).getText().replaceAll("[^\\d]", "") : "00";
                    String combined = intPart + "." + fracPart;
                    prices.add(new BigDecimal(combined));
                }
            } catch (Exception ignored) { /* some tiles may have no price */ }
        }
        return prices;
    }

    /** Extract brand from tile title line (heuristic; may vary). */
    public List<String> getVisibleBrands(int limit) {
        List<String> brands = new ArrayList<>();
        int count = tiles == null ? 0 : tiles.size();
        for (int i = 0; i < Math.min(limit, count); i++) {
            try {
                WebElement t = tiles.get(i);
                WebElement titleEl = t.findElement(By.cssSelector("h2 a span"));
                String title = titleEl.getText().trim();
                // Heuristic: brand often appears at start; keep entire title for contains-check
                brands.add(title);
            } catch (Exception ignored) {}
        }
        return brands;
    }

    /** Extract star rating value text (e.g., "4.3 out of 5 stars"). */
    public List<Double> getVisibleRatings(int limit) {
        List<Double> ratings = new ArrayList<>();
        int count = tiles == null ? 0 : tiles.size();
        for (int i = 0; i < Math.min(limit, count); i++) {
            try {
                WebElement t = tiles.get(i);
                WebElement ratingEl = t.findElement(By.cssSelector("span.a-icon-alt"));
                String text = ratingEl.getText(); // e.g., "4.5 out of 5 stars"
                String num = text.split(" ")[0].trim();
                ratings.add(Double.parseDouble(num));
            } catch (Exception ignored) { /* tile may lack rating */ }
        }
        return ratings;
    }

}
