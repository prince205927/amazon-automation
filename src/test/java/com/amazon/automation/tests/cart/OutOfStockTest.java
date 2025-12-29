package com.amazon.automation.tests.cart;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.ProductDetailsPage;
import com.amazon.automation.pages.ProductDetailsPage.VariationCombination;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class OutOfStockTest extends BaseTest {
	@Test
    public void verifyOutOfStockVariationCannotBeAdded() {
        HomePage home = openHomeReady();
        home.searchBar().type("shirt").submitSearch();
        
        SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
        
        System.out.println("\n========== Searching for Out of Stock Variation ==========");
        
        boolean outOfStockFound = false;
        int productIndex = 1;
        int maxAttempts = 12;
        
        while (!outOfStockFound && productIndex < maxAttempts) {
            try {
                System.out.println("\n--- Checking Product " + (productIndex + 1) + " ---");
                
                results.openProductByIndex(productIndex);
                ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
                
                VariationCombination outOfStock = details.selectOutOfStockVariation();
                
                if (outOfStock != null) {
                    System.out.println("✓ Found out of stock variation: " + outOfStock);
                    outOfStockFound = true;
                  
                    boolean canAdd = details.isAddToCartAvailable();
                    boolean hasQuantity = details.isQuantitySelectorAvailable();
                    
                    Assert.assertFalse(canAdd, "Add to Cart should be disabled for out of stock");
                    Assert.assertFalse(hasQuantity, "Quantity selector should be disabled for out of stock");
                    
                    System.out.println("✓ Out of stock validation passed");
                } else {
                    System.out.println("No out of stock variations found, trying next product...");
                    DriverFactory.getDriver().navigate().back();
                    results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
                    productIndex++;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                DriverFactory.getDriver().navigate().back();
                results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
                productIndex++;
            }
        }
        
        if (!outOfStockFound) {
            System.out.println("\n⚠ No out of stock variations found in " + maxAttempts + " products");
            throw new org.testng.SkipException("No out of stock variations found to test");
        }
        
        System.out.println("\n========== OUT OF STOCK TEST PASSED ==========\n");
    }
}