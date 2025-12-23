package com.amazon.automation.tests.cart;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.CartPage;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.ProductDetailsPage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class UpdateQuantityTest extends BaseTest {
	@Test
	public void verifyCartDetails() {
		HomePage home = openHomeReady();
		home.changeLocation("United Kingdom");
		home.searchBar().type("shirt").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		results.openProductByIndex(1);
		ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
		String originalProductTitle = details.getProductName();
		Double originalProductPrice = details.goToVariations().getPrice();
		details.selectQuantity("2");
		Integer originalSelectedQuantity = details.getQuantity();
		details.addToCart();
		CartPage cart = new CartPage(DriverFactory.getDriver());
		Double prevSubtotal = cart.getSubTotalPrice();
		cart.incrementQuantity();
		Double afterIncrementSubtotal = cart.getSubTotalPrice();
		Assert.assertNotEquals(prevSubtotal, afterIncrementSubtotal);
		Integer changedQuantity = cart.getQuantity();
		cart.decrementQuantity();
		Double afterDecrementSubtotal = cart.getSubTotalPrice();
		Assert.assertNotEquals(afterIncrementSubtotal, afterDecrementSubtotal);
		Integer againChangedQuantity = cart.getQuantity();
	}
}
