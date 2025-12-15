package com.amazon.automation.utils;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {
	private final WebDriverWait wait;
	
	public WaitUtils(WebDriver driver, int seconds ) {
		this.wait = new WebDriverWait(driver,Duration.ofSeconds(seconds));
	}
}
