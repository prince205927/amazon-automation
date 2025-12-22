package com.amazon.automation.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import com.amazon.automation.base.BaseComponent;

public class SortingComponent extends BaseComponent {
	public SortingComponent(WebDriver driver) {
		super(driver);
	}

	@FindBy(id = "s-result-sort-select")
	private WebElement sortingElement;

	Select dropdown = new Select(sortingElement);

	public void selectSort(String value) {
		dropdown.selectByValue(value);
	}

}
