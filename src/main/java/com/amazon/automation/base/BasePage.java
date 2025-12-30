
package com.amazon.automation.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.amazon.automation.utils.WaitUtils;


public abstract class BasePage extends AbstractBase {

    protected BasePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }
}

