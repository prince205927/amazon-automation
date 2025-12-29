package com.amazon.automation.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import com.amazon.automation.base.BaseComponent;

public class SortingComponent extends BaseComponent {

	private final By sortingElement = By.id("s-result-sort-select");

	public SortingComponent(WebDriver driver) {
		super(driver);
	}

	private Select getDropdown() {
		return new Select(wait.visible(sortingElement));
	}

	public void selectSort(String value) {
		getDropdown().selectByValue(value);
	}
}
