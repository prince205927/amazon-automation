package com.amazon.automation.tests.filters;

import org.testng.annotations.Test;

import com.amazon.automation.pages.HomePage;
import com.amazon.automation.tests.common.BaseTest;

public class PriceTests extends BaseTest {
	@Test
	public void selectBrand() {
		String brand = "Apple";
		HomePage page = openHomeReady();
		page.searchBar().type("laptop").submitSearch();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		page.filters().clickPriceRange("Up to $350");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
}
}
