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

	public ProductDetailsPage(WebDriver driver) {
		super(driver);
        this.variations = new ProductVariationsComponent(driver);
	}
	 public ProductVariationsComponent variations() {
	        return variations;
	    }
	public ProductDetailsPage waitForResults() {
		wait.visible(By.id("ppd"));
		return this;
	}
	public static class VariationCombination {
        private String color;
        private String size;
        
        public VariationCombination(String color, String size) {
            this.color = color;
            this.size = size;
        }
        
        public String getColor() { return color; }
        public String getSize() { return size; }
        
        @Override
        public String toString() {
            return "Color: " + color + ", Size: " + size;
        }
    }
	
	public List<String> getAvailableColors() {
        List<String> colors = new ArrayList<>();
        try {
            List<WebElement> colorElements = driver.findElements(By.cssSelector("span.image-swatch-button-with-slots img.swatch-image"));
            for (WebElement colorImg : colorElements) {
                String altText = colorImg.getAttribute("alt");
                if (altText != null && !altText.trim().isEmpty()) {
                    colors.add(altText.trim());
                }
            }
        } catch (Exception e) {
            System.out.println("No color variations found or error: " + e.getMessage());
        }
        return colors;
    }
	
	 public List<String> getAvailableSizes() {
	        List<String> sizes = new ArrayList<>();
	        try {
	            List<WebElement> sizeElements = driver.findElements(By.cssSelector("div#inline-twister-row-size_name span.swatch-title-text-display"));
	            for (WebElement size : sizeElements) {
	                String sizeText = size.getAttribute("innerText");
	                if (sizeText != null && !sizeText.trim().isEmpty()) {
	                    sizes.add(sizeText.trim());
	                }
	            }
	        } catch (Exception e) {
	            System.out.println("No size variations found or error: " + e.getMessage());
	        }
	        return sizes;
	    }
	 public List<VariationCombination> getAllVariationCombinations() {
	        List<VariationCombination> combinations = new ArrayList<>();
	        List<String> colors = getAvailableColors();
	        List<String> sizes = getAvailableSizes();
	        
	        // If both variations exist, create combinations
	        if (!colors.isEmpty() && !sizes.isEmpty()) {
	            for (String color : colors) {
	                for (String size : sizes) {
	                    combinations.add(new VariationCombination(color, size));
	                }
	            }
	        } 
	        // If only colors exist
	        else if (!colors.isEmpty()) {
	            for (String color : colors) {
	                combinations.add(new VariationCombination(color, null));
	            }
	        } 
	        // If only sizes exist
	        else if (!sizes.isEmpty()) {
	            for (String size : sizes) {
	                combinations.add(new VariationCombination(null, size));
	            }
	        }
	        
	        System.out.println("Found " + combinations.size() + " variation combinations");
	        return combinations;
	    }
	 
 public VariationCombination selectRandomVariation() {
        List<VariationCombination> combinations = getAllVariationCombinations();
        
        if (combinations.isEmpty()) {
            System.out.println("No variations available, proceeding without selection");
            return new VariationCombination(null, null);
        }
        
        // Select random combination
        Random random = new Random();
        List<VariationCombination> attemptedCombinations = new ArrayList<>();
        int maxAttempts = Math.min(combinations.size(), 5);        
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Select a combination that hasn't been tried yet
            VariationCombination selectedCombination;
            do {
                selectedCombination = combinations.get(random.nextInt(combinations.size()));
            } while (attemptedCombinations.contains(selectedCombination) && attemptedCombinations.size() < combinations.size());
            
            attemptedCombinations.add(selectedCombination);
            System.out.println("Attempt " + (attempt + 1) + " - Trying variation: " + selectedCombination);
            
            // Select the variations
            if (selectedCombination.getColor() != null) {
                chooseColor(selectedCombination.getColor());
            }
            
            if (selectedCombination.getSize() != null) {
                chooseSize(selectedCombination.getSize());
            }
    
            boolean noOOS = !hasOutOfStockMessage();
            boolean variationAvailable = isVariationAvailable();

            if (noOOS && variationAvailable) {
                System.out.println("Variation is available " + selectedCombination);
                return selectedCombination;
            } else {
                System.out.println("✗ Variation unavailable (OOS=" + !noOOS 
                    + ", addToCartAvailable=" + variationAvailable + ")");
            }

        }
        
        System.out.println("⚠ Could not find available variation after " + maxAttempts + " attempts");
        return null;
    }

 private boolean isVariationAvailable() {
	    try {
	        By addToCartButtonLocator = By.id("add-to-cart-button");
	        WebElement addToCartButton = driver.findElement(addToCartButtonLocator);

	        boolean addToCartAvailable = addToCartButton.isDisplayed()
	                && addToCartButton.isEnabled()
	                && !addToCartButton.getAttribute("class").contains("disabled");

	        if (!addToCartAvailable) {
	            return false; 
	        }

	        By quantitySelectLocator = By.cssSelector("select#quantity");
	        List<WebElement> quantityDropdowns = driver.findElements(quantitySelectLocator);

	        if (!quantityDropdowns.isEmpty()) {
	            WebElement quantityDropdown = quantityDropdowns.get(0);
	            return quantityDropdown.isDisplayed() && quantityDropdown.isEnabled();
	        }

	        return true;

	    } catch (Exception e) {
	        System.out.println("Error checking availability: " + e.getMessage());
	        return false;
	    }
	}

	    public ProductDetailsPage selectVariation(String color, String size) {
	        if (color != null && !color.isEmpty()) {
	            chooseColor(color);
	        }
	        if (size != null && !size.isEmpty()) {
	            chooseSize(size);
	        }
	        return this;
	    }
	    
	    public String chooseColor(String text) {
	        try {
	             By availableColorsLocator = By.cssSelector("span.image-swatch-button-with-slots img.swatch-image");
	             By selectedColorTextLocator = By.cssSelector("span#inline-twister-expanded-dimension-text-color_name");
	            List<WebElement> availableColorsImg = driver.findElements(availableColorsLocator);
	            
	            for (WebElement colorImg : availableColorsImg) {
	                String altText = colorImg.getAttribute("alt").trim();
	                if (text.equalsIgnoreCase(altText)) {
	                    // Click the parent clickable element
	                    WebElement clickableDiv = colorImg.findElement(
	                        By.xpath("./ancestor::span[contains(@class,'image-swatch-button-with-slots')]")
	                    );
	                    
	                    // Scroll into view first
	                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", clickableDiv);
	                    
	                    // Click using JavaScript
	                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickableDiv);
	                    
	                    // Wait for selection to reflect
	                    wait.presenceInElement(selectedColorTextLocator, text);
	                    
	                    System.out.println("Selected color: " + altText);
	                    return altText;
	                }
	            }
	            System.out.println("Color '" + text + "' not found");
	        } catch (Exception e) {
	            System.out.println("Error selecting color: " + e.getMessage());
	        }
	        return null;
	    }
	    
	    /**
	     * Choose size variation
	     */
	    public String chooseSize(String text) {
	        try {
	        	By availableSizesLocator = By.cssSelector("div#inline-twister-row-size_name span.swatch-title-text-display");
	            List<WebElement> availableSizes = driver.findElements(availableSizesLocator);
	            Actions actions = new Actions(driver);
	            By selectedSizeTextLocator = By.cssSelector("span#inline-twister-expanded-dimension-text-size_name");
	            for (WebElement size : availableSizes) {
	                String sizeText = size.getAttribute("innerText").trim();
	                
	                if (text.equalsIgnoreCase(sizeText)) {
	                    WebElement clickableElement = size.findElement(
	                        By.xpath("./ancestor::div[contains(@class, 'swatch-title-text-container')]")
	                    );
	                    
	                    // Scroll into view
	                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", clickableElement);	                    
	                    // Try regular click first, fallback to JavaScript
	                    try {
	                        actions.moveToElement(clickableElement).click().perform();
	                    } catch (Exception e) {
	                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", clickableElement);
	                    }
	                    
	                    // Wait for selection to reflect
	                    wait.presenceInElement(selectedSizeTextLocator, text);	                    
	                    System.out.println("Selected size: " + sizeText);
	                    return sizeText;
	                }
	            }
	            System.out.println("Size '" + text + "' not found");
	        } catch (Exception e) {
	            System.out.println("Error selecting size: " + e.getMessage());
	        }
	        return null;
	    }
	    
	    /**
	     * Select quantity - FIXED VERSION
	     */
	    public ProductDetailsPage selectQuantity(String quantity) {
	    	By quantitySelectLocator = By.cssSelector("select#quantity");
	        try {
	            WebElement quantityDropdown = wait.presenceOfElement(quantitySelectLocator);
	            
	            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", quantityDropdown);
	            
	            Select select = new Select(quantityDropdown);
	            
	            try {
	                select.selectByValue(quantity);
	                System.out.println("Selected quantity by value: " + quantity);
	            } catch (Exception e) {
	                // If that fails, try by visible text
	                select.selectByVisibleText(quantity);
	                System.out.println("Selected quantity by visible text: " + quantity);
	            }
	            	            
	        } catch (Exception e) {
	            System.out.println("Error selecting quantity: " + e.getMessage());
	            try {
	                WebElement dropdown = driver.findElement(quantitySelectLocator);
	                ((JavascriptExecutor) driver).executeScript(
	                    "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));", 
	                    dropdown, quantity
	                );
	            } catch (Exception ex) {
	                System.out.println("Alternative quantity selection also failed: " + ex.getMessage());
	            }
	        }
	        return this;
	    }
	    
	    /**
	     * Get currently selected quantity
	     */
	    public Integer getQuantity() {
	        try {
	        	By quantitySelectLocator = By.cssSelector("select#quantity");
	            WebElement selectElement = wait.presenceOfElement(quantitySelectLocator);
	            Select select = new Select(selectElement);
	            String selectedValue = select.getFirstSelectedOption().getText().trim();
	            return Integer.parseInt(selectedValue);
	        } catch (Exception e) {
	            System.out.println("Error getting quantity: " + e.getMessage());
	            return 1; // Default
	        }
	    }
	    
	    /**
	     * Get currently selected color
	     */
	    public String getSelectedColor() {
	        try {
	        	By selectedColorTextLocator = By.cssSelector("span#inline-twister-expanded-dimension-text-color_name");
	            return wait.presenceOfElement(selectedColorTextLocator).getText().trim();
	        } catch (Exception e) {
	            return null;
	        }
	    }
	    
	    /**
	     * Get currently selected size
	     */
	    public String getSelectedSize() {
	        try {
	        	By selectedSizeTextLocator = By.cssSelector("span#inline-twister-expanded-dimension-text-size_name");
	            return wait.presenceOfElement(selectedSizeTextLocator).getText().trim();
	        } catch (Exception e) {
	            return null;
	        }
	    }
	    
	 
	
	public boolean hasTitle() {
		return !driver.findElement(By.id("productTitle")).getText().trim().isEmpty();
	}

	public boolean hasImages() {
		WebElement landingImage = driver.findElement(By.id("landingImage"));
		List<WebElement> images = driver.findElements(By.cssSelector("li.imageThumbnail img"));
		return landingImage.isDisplayed() && images.get(0).getAttribute("src") != null;
	}

	public boolean hasValidRatings() {
		List<WebElement> ratings = driver.findElements(By.cssSelector("a.a-popover-trigger span.a-icon-alt"));
		if (ratings.isEmpty()) {
			System.out.println("No ratings");
			return false;
		}
		// getText didn't work here because text is hidden via CSS
		String partialText = ratings.get(0).getAttribute("innerText").trim().toLowerCase();
		return partialText.contains("out of 5");

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

	// getText didn't work here because text is hidden via CSS
	public boolean hasAvailabilityText() {
		WebElement availabilityText = wait.presenceOfElement(By.cssSelector("div#availability span.a-color-success"));
		return !availabilityText.getAttribute("innerText").trim().isEmpty();
	}

	public void clickToSeeFullView() {
		WebElement fullView = wait.presenceOfElement(By.partialLinkText("Click to see full view"));
		wait.clickable(fullView).click();
		waitForTransformToComplete();
	}

	public void waitForTransformToComplete() {
		wait.visible(By.cssSelector("div.a-popover.a-popover-modal.a-declarative.a-popover-modal-fixed-height"));
		wait.waitUntil(driver -> {
			try {
				WebElement transformingDiv = driver.findElement(
						By.cssSelector("div.a-popover.a-popover-modal.a-declarative.a-popover-modal-fixed-height"));
				String transform = transformingDiv.getCssValue("transform");
				return transform != null && !transform.equals("none");
			} catch (Exception e) {
				return false;
			}
		});
		wait.presenceOfElement(By.className("fullscreen"));
	}

	public ModalComponent goToModal() {
		return new ModalComponent(driver);
	}

	public ProductVariationsComponent goToVariations() {
		return new ProductVariationsComponent(driver);
	}

	public String getProductName() {
		WebElement productNameElement = wait.presenceOfElement(By.id("productTitle"));
		String productName = productNameElement.getAttribute("innerText").trim();
		return productName;
	}
	
	public CartPage addToCart() {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();",
				wait.presenceOfElement(By.cssSelector("input#add-to-cart-button")));
		wait.visible(By.xpath("//h1[contains(.,'Added to cart')]"));
		return new CartPage(driver);
	}
	
	public boolean isOutOfStockMessageVisible() {
		return wait.visible(By.cssSelector("div#outOfStock")).isDisplayed();
	}
	
	public boolean isAddToCartPresent() {
	    try {
	        WebElement addToCartButton =
	            wait.presenceOfElement(By.cssSelector("input#add-to-cart-button"));
	        return addToCartButton.isDisplayed();
	    } catch (TimeoutException e) {
	        return false;
	    }
	}
	/**
	 * Select an out of stock variation intentionally
	 * Returns the first out of stock variation found
	 */
	public VariationCombination selectOutOfStockVariation() {
	    List<VariationCombination> allCombinations = getAllVariationCombinations();
	    
	    if (allCombinations.isEmpty()) {
	        System.out.println("No variations available");
	        return null;
	    }
	    
	    System.out.println("Checking " + allCombinations.size() + " variations for out of stock...");
	    
	    for (VariationCombination combination : allCombinations) {
	        // Select the variation
	        if (combination.getColor() != null) {
	            chooseColor(combination.getColor());
	        }
	        
	        if (combination.getSize() != null) {
	            chooseSize(combination.getSize());
	        }
	        
	        
	        boolean noOOS = hasOutOfStockMessage();
            boolean variationUnavailable = !isVariationAvailable();

            if (noOOS && variationUnavailable) {
                System.out.println("Variation is available " + combination);
                return combination;
            } 
	    }
	    
	    System.out.println("No out of stock variations found");
	    return null;
	}

	/**
	 * Get all out of stock variations
	 */
	public List<VariationCombination> getAllOutOfStockVariations() {
	    List<VariationCombination> outOfStockVariations = new ArrayList<>();
	    List<VariationCombination> allCombinations = getAllVariationCombinations();
	    
	    for (VariationCombination combination : allCombinations) {
	        // Select the variation
	        if (combination.getColor() != null) {
	            chooseColor(combination.getColor());
	        }
	        
	        if (combination.getSize() != null) {
	            chooseSize(combination.getSize());
	        }
	        

	        // Check if this variation is out of stock
	        if (!isVariationAvailable()) {
	            outOfStockVariations.add(combination);
	        }
	    }
	    
	    System.out.println("Found " + outOfStockVariations.size() + " out of stock variations");
	    return outOfStockVariations;
	}

	/**
	 * Check if Add to Cart button is available
	 */
	public boolean isAddToCartAvailable() {
	    try {
	        By addToCartButtonLocator = By.id("add-to-cart-button");
	        WebElement addToCartButton = driver.findElement(addToCartButtonLocator);
	        
	        boolean isDisplayed = addToCartButton.isDisplayed();
	        boolean isEnabled = addToCartButton.isEnabled();
	        boolean hasDisabledClass = addToCartButton.getAttribute("class").contains("disabled");
	        
	        return isDisplayed && isEnabled && !hasDisabledClass;
	    } catch (NoSuchElementException e) {
	        return false;
	    }
	}

	public boolean isQuantitySelectorAvailable() {
	    try {
	        By quantitySelectLocator = By.cssSelector("select#quantity");
	        WebElement quantityDropdown = driver.findElement(quantitySelectLocator);
	        
	        return quantityDropdown.isDisplayed() && quantityDropdown.isEnabled();
	    } catch (NoSuchElementException e) {
	        return false;
	    }
	}

	public boolean hasOutOfStockMessage() {
	    try {
	        return wait.visible(By.cssSelector("div#outOfStock")).isDisplayed();
	    } catch (Exception e) {
	        return false;
	    }
	}

}
