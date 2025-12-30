package com.amazon.automation.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {
	private static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

	public static void initDriver() {
		removeDriver();
		String browser = System.getProperty("browser", "chrome").toLowerCase();
		boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
		
		WebDriver driver = null;
		switch (browser) {
		case "chrome":
			WebDriverManager.chromedriver().setup();
			ChromeOptions chromeOptions = new ChromeOptions();
			if (headless)
				chromeOptions.addArguments("--headless");
			driver = new ChromeDriver(chromeOptions);
			break;

		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			FirefoxOptions FirefoxOptions = new FirefoxOptions();
			if (headless)
				FirefoxOptions.addArguments("--headless");
			driver = new FirefoxDriver(FirefoxOptions);
			break;
		case "edge":
			WebDriverManager.edgedriver().setup();
			EdgeOptions EdgeOptions = new EdgeOptions();
			if (headless)
				EdgeOptions.addArguments("--headless");
			driver = new EdgeDriver(EdgeOptions);
			break;
		default:
			System.out.println("Unsupported browser" + browser);
		}
		driver.manage().window().maximize();
		driverThreadLocal.set(driver);

	}

	public static WebDriver getDriver() {
		WebDriver driver = driverThreadLocal.get();
		if(driver==null) {
			throw new IllegalStateException("WebDriver is not initialized");
		}
		return driver;
	}

	public static void quitDriver() {
		WebDriver driver = driverThreadLocal.get();
		if (driver != null) {
			try {
			driver.quit();
			} catch(Exception e) {
				System.out.println("Error while quitting driver:"+ e.getMessage());
			}finally {
				driverThreadLocal.remove();
			}
		}
	}
	
	public static void removeDriver() {
		quitDriver();
	}
}
