package com.amazon.automation.tests.cart;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.ProductDetailsPage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class OutOfStockTest extends BaseTest {
	@Test
	public void verifyoutOfStock() {
	HomePage home = openHomeReady();
	home.changeLocation("United Kingdom");
	String searchedColor = "Dense Smoke";
	String searchedSize = String.valueOf(10);
	home.searchBar().type("lowmel sneaker").submitSearch();
	SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
	results.openProductByIndex(1);
	ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
	details.goToVariations().chooseColor(searchedColor);
	String sizeText = details.goToVariations().chooseSize(searchedSize);	
	System.out.println("\n\n\nThe size text is "+ sizeText);
	boolean outOfStockMessage = details.isOutOfStockMessageVisible();
	boolean presenceOfAddToCart = details.isAddToCartPresent();
	Assert.assertNotEquals(presenceOfAddToCart, outOfStockMessage, "If out of stock is present, then add to cart cannot be there and vice versa ");
	}	
}

