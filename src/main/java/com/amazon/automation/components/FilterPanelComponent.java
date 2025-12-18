package com.amazon.automation.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.amazon.automation.base.BaseComponent;
import com.amazon.automation.utils.WaitUtils;

public class FilterPanelComponent extends BaseComponent {

	public FilterPanelComponent(WebDriver driver) {
		super(driver);
	}

	public WebElement findBrand(String brand) {
		String xpath = "//ul[@id='filter-p_123']" + "//a[normalize-space()='" + brand + "']";
		WebElement foundBrand = wait.clickable(By.xpath(xpath));
		return foundBrand;
	}
	
	public WebElement findRatings(String state) {
		if("before".equalsIgnoreCase(state)) {
		return wait.clickable(By.cssSelector("a[aria-label='Apply 4 Stars & Up filter to narrow results']"));
		}
		else
			return wait.clickable(By.cssSelector("a[aria-label='Remove 4 Stars & Up filter to expand results']"));
	}
	public void clickRatings() {
		findRatings("before").click();
	}

	public void clickBrand(String brand) {
		findBrand(brand).click();
	}

	public boolean selectedStateCheck(String brand) {
		return Boolean.parseBoolean(findBrand(brand).getAttribute("aria-current"));
	}
	
	public boolean selectedStateCheck() {
	    return Boolean.parseBoolean(findRatings("after").getAttribute("aria-current"));
	}

}
