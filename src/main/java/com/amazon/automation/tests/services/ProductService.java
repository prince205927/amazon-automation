package com.amazon.automation.tests.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.amazon.automation.pages.CartPage;
import com.amazon.automation.tests.models.CartExpectedItems;
import com.amazon.automation.tests.models.ProductData;
import com.amazon.automation.utils.ExtentReportLogger;
import com.amazon.automation.utils.LoggerUtil;



public class ProductService {
   
    public static class VerificationResult {
        private boolean passed;
        private List<String> errors;
        private String message;
        
        public VerificationResult() {
            this.errors = new ArrayList<>();
            this.passed = true;
        }
        
        public void addError(String error) {
            this.errors.add(error);
            this.passed = false;
        }
        
        public boolean isPassed() {
            return passed;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getErrorSummary() {
            if (passed) {
                return "All verifications passed";
            }
            StringBuilder sb = new StringBuilder("Verification failed with " + errors.size() + " error(s):\n");
            for (int i = 0; i < errors.size(); i++) {
                sb.append((i + 1)).append(". ").append(errors.get(i)).append("\n");
            }
            return sb.toString();
        }
    }
    
    public static VerificationResult verifyCart(CartExpectedItems expectedCart, CartPage cart) {
        VerificationResult result = new VerificationResult();
        LoggerUtil.info("========== Starting Cart Verification =========="); 
        ExtentReportLogger.logStep("Verifying shopping cart");
        List<ProductData> actualCartProducts = cart.getAllCartProducts();
        List<ProductData> expectedProducts = expectedCart.getExpectedProducts();
        
       
       
        // Verification 1: Check cart line item count

        LoggerUtil.info("Verifying cart line item count");
        if (actualCartProducts.size() != expectedProducts.size()) {
            String error = String.format("Cart line item count mismatch. Expected: %d, Actual: %d", 
                expectedProducts.size(), actualCartProducts.size());

LoggerUtil.error(error);
        ExtentReportLogger.fail(error);

        } else {

LoggerUtil.info("Line item count matches: " + actualCartProducts.size());
        ExtentReportLogger.pass(
                "Line item count matches: " + actualCartProducts.size());
        }
        
        // Verification 2: Check total quantity

        LoggerUtil.info("Verifying total quantity");
        int actualTotalQuantity = actualCartProducts.stream()
            .mapToInt(ProductData::getQuantity)
            .sum();
        int expectedTotalQuantity = expectedCart.getTotalExpectedQuantity();
        
        if (actualTotalQuantity != expectedTotalQuantity) {
            String error = String.format("Total quantity mismatch. Expected: %d, Actual: %d", 
                expectedTotalQuantity, actualTotalQuantity);
            result.addError(error);

            LoggerUtil.error(error);
                 ExtentReportLogger.fail(error);
        } else {

LoggerUtil.info(
                "Total quantity matches: " + actualTotalQuantity);
        ExtentReportLogger.pass(
                "Total quantity matches: " + actualTotalQuantity);
        }
        
        // Verification 3: Check each product details

        LoggerUtil.info("Verifying individual cart products");
        for (ProductData expectedProduct : expectedProducts) {
            ProductData matchingProduct = findMatchingProduct(actualCartProducts, expectedProduct);
            
            if (matchingProduct == null) {
                String error = "Product not found in cart: " + expectedProduct.getTitle();

                LoggerUtil.error(error);
                         ExtentReportLogger.fail(error);

                continue;
            }
            

LoggerUtil.info("Verifying product: " + expectedProduct.getTitle());
        ExtentReportLogger.info(
                "Verifying product: " + expectedProduct.getTitle());
            
            // Verify price
            if (!matchingProduct.getPrice().equals(expectedProduct.getPrice())) {
                String error = String.format("Price mismatch for '%s'. Expected: $%.2f, Actual: $%.2f", 
                    expectedProduct.getTitle(), expectedProduct.getPrice(), matchingProduct.getPrice());
                result.addError(error);

                LoggerUtil.error(error);
                         ExtentReportLogger.fail(error);
            } else {

            	 LoggerUtil.info(
            	                    "Price matches: $" + matchingProduct.getPrice());
            	            ExtentReportLogger.pass(
            	                    "Price matches: $" + matchingProduct.getPrice());
            }
            
            // Verify quantity
            if (!matchingProduct.getQuantity().equals(expectedProduct.getQuantity())) {
                String error = String.format("Quantity mismatch for '%s'. Expected: %d, Actual: %d", 
                    expectedProduct.getTitle(), expectedProduct.getQuantity(), matchingProduct.getQuantity());
                result.addError(error);

                LoggerUtil.error(error);
                          ExtentReportLogger.fail(error);
            } else {

            	 LoggerUtil.info(
            	                    "Quantity matches: " + matchingProduct.getQuantity());
            	            ExtentReportLogger.pass(
            	                    "Quantity matches: " + matchingProduct.getQuantity());
            }
        }
        
        if (result.isPassed()) {

LoggerUtil.info("All cart verifications passed");
        ExtentReportLogger.pass("All cart verifications passed");
        } else {

            LoggerUtil.warn(
                        "Cart verification failed with "
                                + result.getErrors().size() + " error(s)");
                ExtentReportLogger.logStep(
                        "Cart verification failed with "
                                + result.getErrors().size() + " error(s)");

        }

        LoggerUtil.info("========== Cart Verification Completed ==========");
        
        return result;
    }
    
    private static ProductData findMatchingProduct(List<ProductData> cartProducts, ProductData expectedProduct) {
        for (ProductData cartProduct : cartProducts) {
            if (matchTitles(cartProduct.getTitle(), expectedProduct.getTitle())) {
                return cartProduct;
            }
        }
        return null;
    }

    private static boolean matchTitles(String actualTitle, String expectedTitle) {
        if (actualTitle == null || expectedTitle == null) {
            return false;
        }
        
        String actual = actualTitle.trim().toLowerCase();
        String expected = expectedTitle.trim().toLowerCase();
        
        // Check if either title contains the other
        return actual.contains(expected) || expected.contains(actual);
    }

    public static void logCartComparison(CartExpectedItems expectedCart, CartPage cart) {
        System.out.println("\n========== Cart Comparison ==========");
        
        System.out.println("\n--- Expected Cart Items ---");
        expectedCart.getExpectedProducts().forEach(p -> 
            System.out.println(String.format("  • %s | $%.2f | Qty: %d | Source: %s", 
                p.getTitle(), p.getPrice(), p.getQuantity(), p.getSource()))
        );
        
        System.out.println("\n--- Actual Cart Items ---");
        cart.getAllCartProducts().forEach(p -> 
            System.out.println(String.format("  • %s | $%.2f | Qty: %d", 
                p.getTitle(), p.getPrice(), p.getQuantity()))
        );
        
        System.out.println("\n--- Summary ---");
        System.out.println("Expected Items: " + expectedCart.getExpectedLineItemCount());
        System.out.println("Actual Items: " + cart.getAllCartProducts().size());
        System.out.println("Expected Total Qty: " + expectedCart.getTotalExpectedQuantity());
        System.out.println("Expected Total Value: $" + String.format("%.2f", expectedCart.getTotalExpectedValue()));
        System.out.println("==========================================\n");
    }

    public static String getCartSummary(CartExpectedItems expectedCart, CartPage cart) {
        StringBuilder summary = new StringBuilder();
        summary.append("Cart Verification Summary:\n");
        summary.append("Expected Items: ").append(expectedCart.getExpectedLineItemCount()).append("\n");
        summary.append("Actual Items: ").append(cart.getAllCartProducts().size()).append("\n");
        summary.append("Expected Total Qty: ").append(expectedCart.getTotalExpectedQuantity()).append("\n");
        
        List<ProductData> actualProducts = cart.getAllCartProducts();
        int actualTotalQty = actualProducts.stream().mapToInt(ProductData::getQuantity).sum();
        summary.append("Actual Total Qty: ").append(actualTotalQty).append("\n");
        
        return summary.toString();
    }
}