package com.amazon.automation.tests.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartExpectedItems {
    private List<ProductData> expectedProducts;
    
    public CartExpectedItems() {
        this.expectedProducts = new ArrayList<>();
    }
    
    public void addProduct(ProductData product) {
        if (product != null) {
            expectedProducts.add(product);
            System.out.println("Added to expected cart: " + product.getTitle() + 
                             " | Price: " + product.getPrice() + 
                             " | Qty: " + product.getQuantity() + 
                             " | Source: " + product.getSource());
        }
    }
    

    public List<ProductData> getExpectedProducts() {
        return new ArrayList<>(expectedProducts);
    }
    

    public int getExpectedLineItemCount() {
        return expectedProducts.size();
    }

    public int getTotalExpectedQuantity() {
        return expectedProducts.stream()
                .mapToInt(ProductData::getQuantity)
                .sum();
    }

    public Double getTotalExpectedValue() {
        return expectedProducts.stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
    }

    public ProductData findProductByTitle(String title) {
        return expectedProducts.stream()
                .filter(p -> p.getTitle().contains(title) || title.contains(p.getTitle()))
                .findFirst()
                .orElse(null);
    }

    public void clear() {
        expectedProducts.clear();
    }

    public boolean isEmpty() {
        return expectedProducts.isEmpty();
    }
    
    public void printSummary() {
        System.out.println("\n========== Expected Cart Summary ==========");
        System.out.println("Total Line Items: " + getExpectedLineItemCount());
        System.out.println("Total Quantity: " + getTotalExpectedQuantity());
        System.out.println("Total Value: $" + String.format("%.2f", getTotalExpectedValue()));
        System.out.println("\nProducts:");
        for (int i = 0; i < expectedProducts.size(); i++) {
            ProductData product = expectedProducts.get(i);
            System.out.println((i + 1) + ". " + product.getTitle() + 
                             " | $" + product.getPrice() + 
                             " | Qty: " + product.getQuantity() + 
                             " | From: " + product.getSource());
        }
        System.out.println("==========================================\n");
    }
    
    @Override
    public String toString() {
        return "CartExpectedItems{" +
                "lineItems=" + getExpectedLineItemCount() +
                ", totalQuantity=" + getTotalExpectedQuantity() +
                ", totalValue=" + getTotalExpectedValue() +
                '}';
    }
}