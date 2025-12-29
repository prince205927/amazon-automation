package com.amazon.automation.tests.cart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.CartPage;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.ProductDetailsPage;
import com.amazon.automation.pages.ProductDetailsPage.VariationCombination;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;
import com.amazon.automation.tests.models.CartExpectedItems;
import com.amazon.automation.tests.models.ProductData;


public class AddToCartTest extends BaseTest {
	@Test
	public void verifyMultipleCartDetailsFromPDP() {
		CartExpectedItems expectedCart = new CartExpectedItems();

		HomePage home = openHomeReady();
		home.changeLocation("United Kingdom");
		home.searchBar().type("shirt").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		int numberOfProducts = 2;
		int[] quantities = { 2, 1};
		int productIndex = 1;
		int addedCount = 0;
		// adding product with random valid variations
		while (addedCount < numberOfProducts) {
			results.openProductByIndex(productIndex);
			ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
			VariationCombination selectedVariation = details.selectRandomVariation();

			// skipping product if no available variations is valid
			if (selectedVariation == null) {
				DriverFactory.getDriver().navigate().back();
				results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
				productIndex++;
				continue;
			}

		    details.selectQuantity(String.valueOf(quantities[addedCount]));


			ProductData expectedProduct = details.captureProductDetails();
			expectedCart.addProduct(expectedProduct);

			details.addToCart();
			addedCount++;
			// navigating back to search results
			DriverFactory.getDriver().navigate().back();
			DriverFactory.getDriver().navigate().back();
			results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
			productIndex++;
		}

		CartPage cart = new CartPage(DriverFactory.getDriver());
		cart.navigateToCart();

		// printing expected cart summary
		expectedCart.printSummary();
		SoftAssert softAssert = new SoftAssert();

		// getting actual cart products
		List<ProductData> actualCartProducts = cart.getAllCartProducts();
		List<ProductData> expectedProducts = expectedCart.getExpectedProducts();

		// verification of cart item count
		int expectedCount = expectedProducts.size();
		int actualCount = actualCartProducts.size();
		softAssert.assertEquals(actualCount, expectedCount,
				"Cart item count mismatch! Expected: " + expectedCount + ", Actual: " + actualCount);
		System.out.println("\n Cart Count Check: Expected=" + expectedCount + ", Actual=" + actualCount);

		// reversing the expected list since cart shows last added item first
		List<ProductData> expectedReversed = new ArrayList<>(expectedProducts);
		Collections.reverse(expectedReversed);

		// verification of each product
		for (int i = 0; i < expectedCount && i < actualCount; i++) {
			ProductData expected = expectedReversed.get(i);
			ProductData actual = actualCartProducts.get(i);

			System.out.println("\n--- Verifying Product " + (i + 1) + " ---");
			System.out.println("Expected: " + expected.getTitle());
			System.out.println("Actual: " + actual.getTitle());

			System.out.println("\n--- Product " + (i + 1) + " ---");
	        System.out.println("Expected: " + expected.getTitle().substring(0, Math.min(60, expected.getTitle().length())) + "...");
	        System.out.println("Actual:   " + actual.getTitle().substring(0, Math.min(60, actual.getTitle().length())) + "...");

			// price verification
			softAssert.assertEquals(actual.getPrice(), expected.getPrice(), "Product " + (i + 1) + " price mismatch!");
			System.out.println("Price: " + (actual.getPrice().equals(expected.getPrice()) ? " PASS" : " FAIL")
					+ " (Expected: $" + expected.getPrice() + ", Actual: $" + actual.getPrice() + ")");

			// quantity verification
			softAssert.assertEquals(actual.getQuantity(), expected.getQuantity(),
					"Product " + (i + 1) + " quantity mismatch!");
			System.out
					.println("Quantity: " + (actual.getQuantity().equals(expected.getQuantity()) ? " PASS" : " FAIL")
							+ " (Expected: " + expected.getQuantity() + ", Actual: " + actual.getQuantity() + ")");
		}

		//asserting all soft assertions
		softAssert.assertAll();
	}
	
	@Test
	public void verifyMultipleCartDetailsFromProductListings() {
	    CartExpectedItems expectedCart = new CartExpectedItems();
	    
	    HomePage home = openHomeReady();
	    home.changeLocation("United Kingdom");
	    home.searchBar().type("earbuds").submitSearch();
	    SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
	    
	    int targetProducts = 5;
	    int addedCount = 0;
	    int maxAttempts = 15; 
	    
	    System.out.println("\n========== Adding Products from Search Listing ==========");
	    System.out.println("Target: " + targetProducts + " products\n");
	    
	    for (int i = 0; i < maxAttempts && addedCount < targetProducts; i++) {
	        System.out.println("\n--- Product Index: " + i + " ---");
	       
	        if (!results.hasAddToCartButtonByIndex(i)) {
	            System.out.println("⊘ No Add to Cart button - skipping");
	            continue;
	        }
	        
	        try {
	            ProductData expectedProduct = results.getProductDataFromListingByIndex(i);
	            
	            if (expectedProduct.getPrice() == 0.0) {
	                System.out.println(" Price not found - skipping to avoid comparison issues");
	                continue;
	            }
	            
	            results.addToCartFromListingByIndex(i);
	            
	            expectedCart.addProduct(expectedProduct);
	            addedCount++;
	            
	            System.out.println("✓ Successfully added product " + addedCount + " of " + targetProducts);
	            
	            
	        } catch (Exception e) {
	            System.out.println("✗ Error at index " + i + ": " + e.getMessage());
	        }
	    }
	    
	    CartPage cart = new CartPage(DriverFactory.getDriver());
	    cart.navigateToCart();
	    cart.waitForResults();
	    
	    expectedCart.printSummary();
	    
	    SoftAssert softAssert = new SoftAssert();
	    
	    List<ProductData> actualCartProducts = cart.getAllCartProducts();
	    List<ProductData> expectedProducts = expectedCart.getExpectedProducts();
	    
	    int expectedCount = expectedProducts.size();
	    int actualCount = actualCartProducts.size();
	    
	    System.out.println("\n========== CART VERIFICATION ==========");
	    System.out.println("Expected items: " + expectedCount);
	    System.out.println("Actual items: " + actualCount);
	    
	    softAssert.assertEquals(actualCount, expectedCount,
	            "Cart item count mismatch!");
	    
	    if (actualCount == 0) {
	        System.out.println("\n CRITICAL: Cart is empty! No items were added.");
	        softAssert.assertAll();
	        return;
	    }
	    
	    List<ProductData> expectedReversed = new ArrayList<>(expectedProducts);
	    Collections.reverse(expectedReversed);
	    
	    int itemsToCheck = Math.min(expectedCount, actualCount);
	    for (int i = 0; i < itemsToCheck; i++) {
	        ProductData expected = expectedReversed.get(i);
	        ProductData actual = actualCartProducts.get(i);
	        
	        System.out.println("\n--- Product " + (i + 1) + " ---");
	        System.out.println("Expected: " + expected.getTitle().substring(0, Math.min(60, expected.getTitle().length())) + "...");
	        System.out.println("Actual:   " + actual.getTitle().substring(0, Math.min(60, actual.getTitle().length())) + "...");
	        
	        double expectedPrice = expected.getPrice();
	        double actualPrice = actual.getPrice();
	        double priceDelta = 0.01;
	        boolean priceMatches = Math.abs(actualPrice - expectedPrice) < priceDelta;
	        
	        softAssert.assertTrue(priceMatches, 
	                "Product " + (i + 1) + " price mismatch! Expected: $" + expectedPrice + ", Actual: $" + actualPrice);
	        System.out.println("Price: " + (priceMatches ? "✓ PASS" : "✗ FAIL") 
	                + " (Expected: $" + expectedPrice + ", Actual: $" + actualPrice + ")");
	        
	        Integer expectedQty = expected.getQuantity();
	        Integer actualQty = actual.getQuantity();
	        boolean qtyMatches = expectedQty.equals(actualQty);
	        
	        softAssert.assertEquals(actualQty, expectedQty,
	                "Product " + (i + 1) + " quantity mismatch!");
	        System.out.println("Quantity: " + (qtyMatches ? "✓ PASS" : "✗ FAIL")
	                + " (Expected: " + expectedQty + ", Actual: " + actualQty + ")");
	    }
	    
	    // Assert all
	    softAssert.assertAll();
	    
	    System.out.println("\n========== ✓ TEST PASSED ==========\n");
	}
}
