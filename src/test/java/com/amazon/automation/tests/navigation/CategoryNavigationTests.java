package com.amazon.automation.tests.navigation;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.tests.common.BaseTest;

public class CategoryNavigationTests extends BaseTest {
	@Test
	public void debugMenuStructureTest() {
		String categoryText = "Arts & Crafts";
		String subCategoryText = "Crafting";
		HomePage home = openHomeReady();
		// Opening menu
		home.categoryMenu().openMenu();
		// Clicking category using the component method
		home.categoryMenu().clickCategory(categoryText);
		// Clicking subcategory
		home.categoryMenu().clickSubCategory(subCategoryText);
		// Assertions
		String currentUrl = DriverFactory.getDriver().getCurrentUrl();
		Assert.assertFalse(currentUrl.contains("ref=nav_em_linktree_fail"),
				"Navigation should not result in error page");

		Assert.assertTrue(currentUrl.toLowerCase().contains(subCategoryText.toLowerCase()),
				"Should navigate to url containing the subcategory text " + currentUrl);

	}
}
