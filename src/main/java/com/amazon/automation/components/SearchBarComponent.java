package com.amazon.automation.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.amazon.automation.utils.WaitUtils;

public class SearchBarComponent {
	private final WebDriver driver; 
	private final WaitUtils wait;
	
	@FindBy(id = "twotabsearchtextbox")
	private WebElement searchBox;
	
	@FindBy(id = "nav-search-submit-button")
	private WebElement submitButton;
	
	public SearchBarComponent(WebDriver driver) {
		this.driver = driver;
		this.wait = new WaitUtils(driver,15);
		PageFactory.initElements(driver, this);
	}
	
	public SearchBarComponent type(String text) {
		WebElement box = wait.visible(searchBox);
		box.clear();
		box.sendKeys(text);
		return this;
	}
	
	public void submitSearch() {
		wait.visible(submitButton).click();
	}
	
}
