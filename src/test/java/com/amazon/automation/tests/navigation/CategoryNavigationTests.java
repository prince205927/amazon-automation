package com.amazon.automation.tests.navigation;

import static com.amazon.automation.tests.testdata.TestData.*;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.tests.common.BaseTest;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

@Listeners(com.amazon.automation.tests.listeners.ExtentTestListener.class)

public class CategoryNavigationTests extends BaseTest {
	@Test(groups = { "navigation", "smoke", "regression",
			"p0" }, priority = 1, description = "Verify category navigation by visible text")
	public void verifyCategoryNavigationByVisibleText() {

		LoggerUtil.info("===== Test Started: verifyCategoryNavigationByVisibleText =====");
		ExtentReportLogger.logStep("Verifying category navigation using visible text");

		HomePage home = openHomeReady();
		// opening menu and clicking categories and subcategories

		LoggerUtil.info(
				"Opening category menu and navigating to category: " + CATEGORY + " -> subcategory: " + SUBCATEGORY);

		home.categoryMenu().openMenu().waitForMenuToOpen().clickCategory(CATEGORY).clickSubCategory(SUBCATEGORY);

		// Assertions
		String currentUrl = DriverFactory.getDriver().getCurrentUrl();
		Assert.assertFalse(currentUrl.contains("ref=nav_em_linktree_fail"),
				"Navigation should not result in error page");

		Assert.assertTrue(currentUrl.toLowerCase().contains(SUBCATEGORY.toLowerCase()),
				"Should navigate to url containing the subcategory text " + currentUrl);

		ExtentReportLogger.pass("Category navigation by visible text verified successfully");

	}

	@Test(groups = { "navigation", "regression",
			"p1" }, priority = 2, description = "Verify category navigation by index")
	public void verifyCategoryNavigationByIndex() {

		LoggerUtil.info("===== Test Started: verifyCategoryNavigationByIndex =====");
		ExtentReportLogger.logStep("Verifying category navigation using index selection");

		HomePage home = openHomeReady();
		String title = home.categoryMenu().openMenu().waitForMenuToOpen().clickCategoryByIndex(10)
				.clickSubCategoryByIndex(4);
		String currentUrl = DriverFactory.getDriver().getCurrentUrl();
		Assert.assertFalse(currentUrl.contains("ref=nav_em_linktree_fail"),
				"Navigation should not result in error page");

		Assert.assertTrue(currentUrl.toLowerCase().contains(title.toLowerCase()),
				"Should navigate to url containing the subcategory text " + currentUrl);

		ExtentReportLogger.pass("Category navigation by index verified successfully");

	}

}
