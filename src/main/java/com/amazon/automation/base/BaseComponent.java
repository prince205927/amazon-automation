package com.amazon.automation.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.amazon.automation.utils.WaitUtils;


public abstract class BaseComponent extends AbstractBase {

    protected BaseComponent(WebDriver driver) {
        super(driver);
    }
}
