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
		int numberOfProducts = 5;
		int[] quantities = { 2, 1, 3, 4, 5 };
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

			//title verification
			// Extract first word from expected title
			String expectedFirstWord = expected.getTitle()
			        .trim()
			        .split("\\s+")[0]
			        .toLowerCase();

			// Extract first word from actual title
			String actualFirstWord = actual.getTitle()
			        .trim()
			        .split("\\s+")[0]
			        .toLowerCase();

			boolean titleMatches = actualFirstWord.equals(expectedFirstWord);

			softAssert.assertTrue(
			        titleMatches,
			        "Product " + (i + 1) + " title mismatch! Expected first word: '"
			                + expectedFirstWord + "', Actual first word: '" + actualFirstWord + "'"
			);

			System.out.println(
			        "Title: " + (titleMatches ? " PASS" : " FAIL")
			                + " (Expected first word: '" + expectedFirstWord + "')"
			);

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
	public void verifyMultipleCartDetailsFromProductListing() {
	    CartExpectedItems expectedCart = new CartExpectedItems();

	    HomePage home = openHomeReady();
	    home.changeLocation("United Kingdom");
	    home.searchBar().type("laptop").submitSearch();
	    SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
	    
	    int numberOfProducts = 5;
	    int addedCount = 0;
	    
	    System.out.println("\n========== Adding Products from Search Listing ==========");
	    
	   
	    for (int i = 0; i < 10 && addedCount < numberOfProducts; i++) {
	        int productIndex = i+1;
	        
	        System.out.println("\n--- Checking Product Index: " + productIndex + " ---");
	        
	        // checking if this product has "Add to cart" button in listing
	        if (!results.hasAddToCartButtonByIndex(productIndex)) {
	            System.out.println("Product " + productIndex + " doesn't have Add to Cart button in listing, skipping...");
	            continue;
	        }
	        
	        try {
	        
	            ProductData expectedProduct = results.getProductDataFromListingByIndex(productIndex);
	           
	            results.addToCartFromListingByIndex(productIndex, expectedProduct.getAsin());

	            expectedCart.addProduct(expectedProduct);
	      
	            addedCount++;
	            
	            System.out.println(" Successfully added product " + addedCount + " of " + numberOfProducts);
	           
	        } catch (Exception e) {
	            System.out.println("Error adding product " + productIndex + ": " + e.getMessage());
	            continue;
	        }
	    }
	    
	    if (addedCount < numberOfProducts) {
	        System.out.println("\n Warning: Only added " + addedCount + " products out of " + 
	            numberOfProducts + " requested (not all products had Add to Cart in listing)");
	    }
	    
	    CartPage cart = new CartPage(DriverFactory.getDriver());
	    cart.navigateToCart();
	    cart.waitForResults();

	    expectedCart.printSummary();
	    
	    SoftAssert softAssert = new SoftAssert();

	    // getting actual cart products
	    List<ProductData> actualCartProducts = cart.getAllCartProducts();
	    List<ProductData> expectedProducts = expectedCart.getExpectedProducts();

	    // verifying cart item count
	    int expectedCount = expectedProducts.size();
	    int actualCount = actualCartProducts.size();
	    softAssert.assertEquals(actualCount, expectedCount,
	            "Cart item count mismatch! Expected: " + expectedCount + ", Actual: " + actualCount);
	    System.out.println("\nCart Count Check: Expected=" + expectedCount + ", Actual=" + actualCount);

	    // reverse the expected list since cart shows last added item first
	    List<ProductData> expectedReversed = new ArrayList<>(expectedProducts);
	    Collections.reverse(expectedReversed);
	    
	    // verify each product
	    for (int i = 0; i < expectedCount && i < actualCount; i++) {
	        ProductData expected = expectedReversed.get(i);
	        ProductData actual = actualCartProducts.get(i);

	        System.out.println("\n--- Verifying Product " + (i + 1) + " ---");
	        System.out.println("Expected: " + expected.getTitle());
	        System.out.println("Actual: " + actual.getTitle());

	        // Price verification
	        double priceDelta = 0.01;
	        boolean priceMatches = Math.abs(actual.getPrice() - expected.getPrice()) < priceDelta;
	        
	        softAssert.assertTrue(priceMatches, "Product " + (i + 1) + " price mismatch!");
	        System.out.println("Price: " + (priceMatches ? "PASS" : "FAIL")
	                + " (Expected: $" + expected.getPrice() + ", Actual: $" + actual.getPrice() + ")");

	        // Quantity verification (should be 1 for listing additions)
	        softAssert.assertEquals(actual.getQuantity(), expected.getQuantity(),
	                "Product " + (i + 1) + " quantity mismatch!");
	        System.out.println("Quantity: " + (actual.getQuantity().equals(expected.getQuantity()) ? "PASS" : "FAIL")
	                + " (Expected: " + expected.getQuantity() + ", Actual: " + actual.getQuantity() + ")");
	    }

	    // Assert all soft assertions
	    softAssert.assertAll();
	    
	    System.out.println("\n========== LISTING ADD TO CART TEST PASSED ==========\n");
	}
}
