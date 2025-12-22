package com.amazon.automation.tests.details;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.amazon.automation.base.DriverFactory;
import com.amazon.automation.pages.HomePage;
import com.amazon.automation.pages.ProductDetailsPage;
import com.amazon.automation.pages.SearchResultsPage;
import com.amazon.automation.tests.common.BaseTest;

public class ZoomAndSwitchTests extends BaseTest {
	@Test
	public void verifySwitch() {
		HomePage home = openHomeReady();
		home.searchBar().type("laptop").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		results.openProductByIndex(1);
		ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
		String urlBefore = DriverFactory.getDriver().getCurrentUrl();
		details.clickToSeeFullView();
		//assertion1 (url should be same as modal is only rendered, no reload)
		Assert.assertEquals(DriverFactory.getDriver().getCurrentUrl(), urlBefore, "Url shouldn't change in full view");
		//assertion2 (validating if modal is open)
		Assert.assertTrue(details.goToModal().isFullViewOpen(),"Modal is not open");
		boolean switchedState = details.goToModal().checkSwitchingBehaviour();
		//assertion3 (validating switching behavior)
		Assert.assertTrue(switchedState,"Switching behavior is not ok");
		
	}
	@Test
	public void verifyZoom() {
		HomePage home = openHomeReady();
		home.searchBar().type("laptop").submitSearch();
		SearchResultsPage results = new SearchResultsPage(DriverFactory.getDriver()).waitForResults();
		results.openProductByIndex(1);
		ProductDetailsPage details = new ProductDetailsPage(DriverFactory.getDriver()).waitForResults();
		details.clickToSeeFullView();
		int heightBeforeZoom = details.goToModal().getImageHeight();
		details.goToModal().zoomInteraction(); //zoom in
		int heightAfterZoom = details.goToModal().getImageHeight();
		//assertion1 (validating zoom in)
		Assert.assertTrue(heightAfterZoom>heightBeforeZoom, "Zoom in failed ");
		details.goToModal().zoomInteraction();//zoom out
		int heightAfterZoomOut = details.goToModal().getImageHeight();
		//asserion2 (validating zoom out)
		Assert.assertTrue(heightAfterZoomOut<heightAfterZoom, "Zoom out failed");

	}
	
	
}
