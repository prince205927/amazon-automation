package com.amazon.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {
    
    private static ThreadLocal<Logger> loggerThreadLocal = new ThreadLocal<>();
    
    public static Logger getLogger() {
        if (loggerThreadLocal.get() == null) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String className = stackTrace[2].getClassName();
            loggerThreadLocal.set(LogManager.getLogger(className));
        }
        return loggerThreadLocal.get();
    }
   
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
    
    public static void removeLogger() {
        loggerThreadLocal.remove();
    }
    

    public static void info(String message) {
        getLogger().info(message);
    }
    
    public static void debug(String message) {
        getLogger().debug(message);
    }
    
    public static void warn(String message) {
        getLogger().warn(message);
    }
    
    public static void error(String message) {
        getLogger().error(message);
    }
    
    public static void error(String message, Throwable throwable) {
        getLogger().error(message, throwable);
    }
    
    public static void fatal(String message) {
        getLogger().fatal(message);
    }
    
    public static void testStart(String testName) {
        getLogger().info("========================================");
        getLogger().info("TEST STARTED: " + testName);
        getLogger().info("========================================");
    }
    
    public static void testEnd(String testName, String status) {
        getLogger().info("========================================");
        getLogger().info("TEST " + status + ": " + testName);
        getLogger().info("========================================");
    }
    
    public static void step(String stepDescription) {
        getLogger().info("→ STEP: " + stepDescription);
    }
    
    public static void assertion(String description, boolean passed) {
        if (passed) {
            getLogger().info("ASSERTION PASSED: " + description);
        } else {
            getLogger().error("ASSERTION FAILED: " + description);
        }
    }
}
