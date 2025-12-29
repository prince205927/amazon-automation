package com.amazon.automation.tests.cart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.testng.Assert;
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

public class RemoveItemTest extends BaseTest {
    
    @Test
    public void verifyRemoveSingleItemFromCart() {
        CartExpectedItems expectedCart = new CartExpectedItems();

        HomePage home = openHomeReady();
        home.changeLocation("United Kingdom");
        home.searchBar().type("shirt").submitSearch();
        SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
        
        int numberOfProducts = 3;
        int[] quantities = { 1, 1, 2};
        int addedCount = 0;
        int productIndex = 1;
        
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

        //navigating to cart
        CartPage cart = new CartPage(DriverFactory.getDriver());
        cart.navigateToCart();
        cart.waitForResults();

        System.out.println("\n========== Before Removal ==========");
        expectedCart.printSummary();
        
        int initialCount = cart.getCartBadgeCount();
        System.out.println("Initial cart count: " + initialCount);

        // removing second item with index1
        int indexToRemove = 1;
        String productToRemove = cart.getProductTitleByIndex(indexToRemove);
        int quantityToRemove = expectedCart.getExpectedProducts().get(indexToRemove).getQuantity();
        System.out.println("\n--- Removing product at index " + indexToRemove + " ---");
        System.out.println("Product: " + productToRemove);
        
        cart.removeItemByIndex(indexToRemove);
        
        // Update expected cart
        List<ProductData> expectedProducts = expectedCart.getExpectedProducts();
        Collections.reverse(expectedProducts);
        expectedProducts.remove(indexToRemove);
        Collections.reverse(expectedProducts);
        
        // Verify removal
        int finalCount = cart.getCartBadgeCount();
        System.out.println("\n--- After Removal ---");
        System.out.println("Final cart count: " + finalCount);
        
        Assert.assertEquals(finalCount, initialCount - quantityToRemove, "Cart count should decrease by 1");
        Assert.assertFalse(cart.isProductInCart(productToRemove), 
            "Removed product should not be in cart");
        
        System.out.println("\n========== REMOVAL TEST PASSED ==========\n");
    }
    
    @Test
    public void verifyRemoveAllItemsFromCart() {
        CartExpectedItems expectedCart = new CartExpectedItems();

        HomePage home = openHomeReady();
        home.changeLocation("United Kingdom");
        home.searchBar().type("shirt").submitSearch();
        SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
        
        int numberOfProducts = 2;
        int addedCount = 0;
        int productIndex = 3;
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

        System.out.println("\n========== Removing All Items ==========");
        
        int itemCount = cart.getCartItemCount();
        System.out.println("Items to remove: " + itemCount);
        
        // Remove all items one by one
        cart.removeAllItems();

        // Verify cart is empty
        Assert.assertTrue(cart.isCartEmpty(), "Cart should be empty after removing all items");
        System.out.println("✓ Cart is empty");
        
        System.out.println("\n========== REMOVE ALL TEST PASSED ==========\n");
    }
}