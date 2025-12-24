package com.amazon.automation.tests.cart;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.CartPage;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.ProductDetailsPage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class RemoveItemTest extends BaseTest {
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
		cart.removeItemFromCart();
		int postBadge = cart.getBadgeCount();
		Double finalSubTotal = cart.getSubTotalPrice();
		String toastContent = cart.getToastMessage();
		//assertion 1 (badge count should be 0) 
		Assert.assertEquals(postBadge, 0, "Cart badge wasn't empty");
		//assertion 2 (subtotal should be 0) 
		Assert.assertEquals(finalSubTotal, 0, "Subtotal wasn't affected");
		//assertion 3 (toast message should contain deleted)
		Assert.assertTrue(toastContent.toLowerCase().contains("removed"),"Removal toast message not shown" );
}
}