package com.amazon.automation.components;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.amazon.automation.base.BaseComponent;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;

public class ModalComponent extends BaseComponent {

	private final By fullViewModal = By
			.cssSelector("div.a-popover.a-popover-modal.a-declarative.a-popover-modal-fixed-height");

	private final By landingImage = By.className("fullscreen");

	private final By thumbnails = By.cssSelector("div.ivRow div.ivThumbImage");

	public ModalComponent(WebDriver driver) {
		super(driver);
	}

	public boolean isFullViewOpen() {
		return wait.presenceOfElement(fullViewModal).getCssValue("display").contains("block");
	}

	public Map<String, String> getImageDetails() {
		WebElement image = wait.presenceOfElement(landingImage);
		Map<String, String> details = new LinkedHashMap<>();
		details.put("src", image.getAttribute("src"));
		details.put("alt", image.getAttribute("alt"));
		details.put("data-old-hires", image.getAttribute("data-old-hires"));
		details.put("currentSrc", image.getAttribute("currentSrc"));
		return details;
	}

	public boolean checkSwitchingBehaviour() {

		LoggerUtil.info("Checking image thumbnail switching behavior");
		ExtentReportLogger.logStep("Verifying image thumbnail switching");

		List<WebElement> thumbnailList = driver.findElements(thumbnails);

		if (thumbnailList.size() <= 1) {
			return false;
		}

		boolean allSwitchesWorked = true;

		for (int i = 1; i < thumbnailList.size(); i++) {
			Map<String, String> beforeClick = getImageDetails();

			WebElement thumbnail = thumbnailList.get(i);
			jsClick(thumbnail);
			WebDriverWait waiter = new WebDriverWait(driver, Duration.ofSeconds(10));
			try {
				waiter.until(ExpectedConditions
						.not(ExpectedConditions.attributeContains(landingImage, "src", beforeClick.get("src"))));

				Map<String, String> afterClick = getImageDetails();

				boolean changed = !beforeClick.get("src").equals(afterClick.get("src"));
				if (!changed) {
					allSwitchesWorked = false;
				}

			} catch (TimeoutException e) {
				allSwitchesWorked = false;
			}
		}

		ExtentReportLogger.info("Thumbnail switching result: " + (allSwitchesWorked ? "SUCCESS" : "FAILURE"));

		return allSwitchesWorked;
	}

	public int getImageHeight() {
		return wait.presenceOfElement(landingImage).getSize().getHeight();
	}

	public int getImageWidth() {
		return wait.presenceOfElement(landingImage).getSize().getWidth();
	}

	public void zoomInteraction() {

		LoggerUtil.info("Performing zoom interaction on image");
		ExtentReportLogger.logStep("Zooming into product image");

		WebElement image = wait.presenceOfElement(landingImage);
		int currentHeight = image.getSize().getHeight();

		Actions action = new Actions(driver);
		action.moveToElement(image).perform();

		image = wait.clickable(landingImage);
		image.click();

		wait.waitUntil(driver -> {
			try {
				WebElement img = driver.findElement(landingImage);
				return img.getSize().getHeight() != currentHeight;
			} catch (StaleElementReferenceException e) {

				LoggerUtil.warn("Stale image element during zoom interaction");

				return false;
			}
		});

		LoggerUtil.info("Zoom interaction completed successfully");
		ExtentReportLogger.pass("Image zoom interaction successful");
	}
}
