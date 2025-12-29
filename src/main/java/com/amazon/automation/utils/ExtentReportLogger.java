package com.amazon.automation.utils;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class ExtentReportLogger {
    
    public static void info(String message) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.INFO, message);
        }
    }
    
    public static void pass(String message) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.PASS, 
                MarkupHelper.createLabel(message, ExtentColor.GREEN));
        }
    }
    
    public static void fail(String message) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.FAIL, 
                MarkupHelper.createLabel(message, ExtentColor.RED));
        }
    }
    
    public static void skip(String message) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.SKIP, 
                MarkupHelper.createLabel(message, ExtentColor.YELLOW));
        }
    }
    
    public static void warning(String message) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.WARNING, 
                MarkupHelper.createLabel(message, ExtentColor.ORANGE));
        }
    }
    
    public static void logStep(String stepDescription) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().info("→ " + stepDescription);
        }
    }
    
    public static void logAssertion(String assertionDescription, boolean passed) {
        if (ExtentManager.getTest() != null) {
            if (passed) {
                ExtentManager.getTest().pass("✓ " + assertionDescription);
            } else {
                ExtentManager.getTest().fail("✗ " + assertionDescription);
            }
        }
    }
}