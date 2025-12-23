package com.amazon.automation.tests.cart;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.CartPage;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.ProductDetailsPage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class AddToCartTest extends BaseTest {
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
		String cartProductTitle = cart.getProductTitle();
		Double cartProductPrice = cart.getUnitPrice();
		Integer cartQuantity = cart.getQuantity();
		// assertion1(validating product title)
		Assert.assertTrue(cartProductTitle.contains(originalProductTitle),
				"The title from product details and title of product from cart donot match");
		// assertion2 (validating product unit price)
		Assert.assertEquals(originalProductPrice, cartProductPrice,
				"The price from product details and unit price of product from cart donot match");
		// assertion 3 (validating quantity)
		Assert.assertEquals(originalSelectedQuantity, cartQuantity,
				"The quantity from product details and quantity of product from cart donot match");

	}
}
