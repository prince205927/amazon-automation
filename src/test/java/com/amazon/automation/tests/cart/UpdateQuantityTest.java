package com.amazon.automation.tests.cart;

import static com.amazon.automation.tests.testdata.TestData.LOCATION_UK;
import static com.amazon.automation.tests.testdata.TestData.SEARCH_SHIRT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Listeners;
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
@Listeners(com.amazon.automation.tests.listeners.ExtentTestListener.class)

public class UpdateQuantityTest extends BaseTest {

	@Test(groups = { "cart", "update", "smoke", "regression",
			"p1" }, priority = 1, description = "Verify update single item quantity")
	public void verifyUpdateSingleItemQuantity() {
		CartExpectedItems expectedCart = new CartExpectedItems();

		HomePage home = openHomeReady();
		home.changeLocation(LOCATION_UK);
		home.searchBar().type(SEARCH_SHIRT).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();

		int numberOfProducts = 2;
		int[] quantities = { 1, 2 };
		int addedCount = 0;
		int productIndex = 3;
		// adding products to cart
		while (addedCount < numberOfProducts) {
			results.openProductByIndex(productIndex);
			ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
			VariationCombination selectedVariation = details.selectRandomVariation();

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

			DriverFactory.getDriver().navigate().back();
			DriverFactory.getDriver().navigate().back();
			results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
			productIndex++;
		}

		// navigating to cart
		CartPage cart = new CartPage(DriverFactory.getDriver());
		cart.navigateToCart();
		cart.waitForResults();

		System.out.println("\n========== Before Update ==========");
		expectedCart.printSummary();

		// updating quantity of first item
		int indexToUpdate = 0;
		int newQuantity = 5;

		int oldQuantity = cart.getQuantityByIndex(indexToUpdate);
		String productTitle = cart.getProductTitleByIndex(indexToUpdate);

		System.out.println("\n--- Updating Product Quantity ---");
		System.out.println("Product: " + productTitle);
		System.out.println("Old Quantity: " + oldQuantity);
		System.out.println("New Quantity: " + newQuantity);

		cart.updateQuantityByIndex(indexToUpdate, newQuantity);

		int updatedQuantity = cart.getQuantityByIndex(indexToUpdate);

		System.out.println("\n--- After Update ---");
		System.out.println("Updated Quantity: " + updatedQuantity);

		Assert.assertEquals(updatedQuantity, newQuantity, "Quantity should be updated to " + newQuantity);

		System.out.println("✓ Quantity updated successfully");
		System.out.println("\n========== UPDATE TEST PASSED ==========\n");
	}

	@Test(groups = { "cart", "update", "regression",
			"p1" }, priority = 2, description = "Verify update multiple items quantities")
	public void verifyUpdateMultipleItemQuantities() {
		CartExpectedItems expectedCart = new CartExpectedItems();

		HomePage home = openHomeReady();
		home.changeLocation(LOCATION_UK);
		home.searchBar().type(SEARCH_SHIRT).submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();

		int numberOfProducts = 2;
		int addedCount = 0;
		int productIndex = 2;
		// Add products to cart
		while (addedCount < numberOfProducts) {
			results.openProductByIndex(productIndex);
			ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
			VariationCombination selectedVariation = details.selectRandomVariation();

			if (selectedVariation == null) {
				DriverFactory.getDriver().navigate().back();
				results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
				productIndex++;
				continue;
			}

			ProductData expectedProduct = details.captureProductDetails();
			expectedCart.addProduct(expectedProduct);
			details.addToCart();
			addedCount++;
			DriverFactory.getDriver().navigate().back();
			DriverFactory.getDriver().navigate().back();
			results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
			productIndex++;
		}

		// Navigate to cart
		CartPage cart = new CartPage(DriverFactory.getDriver());
		cart.navigateToCart();
		cart.waitForResults();

		System.out.println("\n========== Before Updates ==========");
		cart.getAllCartProducts();

		SoftAssert softAssert = new SoftAssert();

		// Update all items with different quantities
		int[] newQuantities = { 3, 2 };

		for (int i = 0; i < cart.getCartItemCount() && i < newQuantities.length; i++) {
			System.out.println("\n--- Updating Item " + (i + 1) + " ---");
			String productTitle = cart.getProductTitleByIndex(i);
			int oldQty = cart.getQuantityByIndex(i);

			System.out.println("Product: " + productTitle);
			System.out.println("Old Quantity: " + oldQty + " -> New Quantity: " + newQuantities[i]);

			cart.updateQuantityByIndex(i, newQuantities[i]);

			int updatedQty = cart.getQuantityByIndex(i);
			softAssert.assertEquals(updatedQty, newQuantities[i], "Item " + (i + 1) + " quantity mismatch");
			System.out.println("✓ Updated successfully to: " + updatedQty);
		}

		softAssert.assertAll();
		System.out.println("\n========== ALL UPDATES TEST PASSED ==========\n");
	}

	@Test(groups = { "cart", "update", "regression",
			"p2" }, priority = 3, description = "Verify increment and decrement buttons")
	public void verifyIncrementDecrementButtons() {
		HomePage home = openHomeReady();
		home.changeLocation("United Kingdom");
		home.searchBar().type("shirt").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();

		// Add one product
		results.openProductByIndex(3);
		ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
		VariationCombination selectedVariation = details.selectRandomVariation();

		if (selectedVariation != null) {
			details.addToCart();
		}

		// Navigate to cart
		CartPage cart = new CartPage(DriverFactory.getDriver());
		cart.navigateToCart();
		cart.waitForResults();

		System.out.println("\n========== Testing Increment/Decrement Buttons ==========");

		int initialQuantity = cart.getQuantityByIndex(0);
		System.out.println("Initial quantity: " + initialQuantity);

		// Test increment
		System.out.println("\n--- Testing Increment ---");
		cart.incrementQuantityByIndex(0);
		int afterIncrement = cart.getQuantityByIndex(0);
		System.out.println("After increment: " + afterIncrement);
		Assert.assertEquals(afterIncrement, initialQuantity + 1, "Quantity should increase by 1");

		// Increment again
		cart.incrementQuantityByIndex(0);
		int afterSecondIncrement = cart.getQuantityByIndex(0);
		System.out.println("After second increment: " + afterSecondIncrement);
		Assert.assertEquals(afterSecondIncrement, initialQuantity + 2, "Quantity should increase by 2");

		// Test decrement
		System.out.println("\n--- Testing Decrement ---");
		cart.decrementQuantityByIndex(0);
		int afterDecrement = cart.getQuantityByIndex(0);
		System.out.println("After decrement: " + afterDecrement);
		Assert.assertEquals(afterDecrement, initialQuantity + 1, "Quantity should decrease by 1");

		// Decrement back to original
		cart.decrementQuantityByIndex(0);
		int finalQuantity = cart.getQuantityByIndex(0);
		System.out.println("Final quantity: " + finalQuantity);
		Assert.assertEquals(finalQuantity, initialQuantity, "Should be back to initial quantity");

		System.out.println("\n========== INCREMENT/DECREMENT TEST PASSED ==========\n");
	}
}