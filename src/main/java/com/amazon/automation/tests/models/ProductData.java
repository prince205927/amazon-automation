package com.amazon.automation.tests.models;

import java.util.Map;
import java.util.Objects;

public class ProductData {
	    private String title;
	    private Double price;
	    private Integer quantity;
	    private String size;
	    private String color;
	    private String asin;
	    private String source; 
	    private int productIndex; 
	    
	    public ProductData() {
	    }
	    
	    public ProductData(String title, Double price, Integer quantity, String size, String color, String source, String asin) {
	        this.title = title;
	        this.price = price;
	        this.quantity = quantity;
	        this.size = size;
	        this.color = color;
	        this.asin=asin;
	        this.source = source;
	    }
	    
	    public String getTitle() {
	        return title;
	    }
	    
	    public void setTitle(String title) {
	        this.title = title;
	    }
	    public String getAsin() {
	    	return asin;
	    }
	    public Double getPrice() {
	        return price;
	    }
	    
	    public void setPrice(Double price) {
	        this.price = price;
	    }
	    
	    public void setAsin(String asin) {
	    	this.asin=asin;
	    }
	    public Integer getQuantity() {
	        return quantity;
	    }
	    
	    public void setQuantity(Integer quantity) {
	        this.quantity = quantity;
	    }
	    
	    public String getSize() {
	        return size;
	    }
	    
	    public void setSize(String size) {
	        this.size = size;
	    }
	    
	    public String getColor() {
	        return color;
	    }
	    
	    public void setColor(String color) {
	        this.color = color;
	    }
	    
	    public String getSource() {
	        return source;
	    }
	    
	    public void setSource(String source) {
	        this.source = source;
	    }
	    
	    public int getProductIndex() {
	        return productIndex;
	    }
	    
	    public void setProductIndex(int productIndex) {
	        this.productIndex = productIndex;
	    }
	    
	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (o == null || getClass() != o.getClass()) return false;
	        ProductData that = (ProductData) o;
	        return Objects.equals(title, that.title) &&
	               Objects.equals(price, that.price) &&
	               Objects.equals(quantity, that.quantity);
	    }
	    
	    @Override
	    public int hashCode() {
	        return Objects.hash(title, price, quantity);
	    }
	    
	    @Override
	    public String toString() {
	        return "ProductData{" +
	                "title='" + title + '\'' +
	                ", price=" + price +
	                ", quantity=" + quantity +
	                ", size='" + size + '\'' +
	                ", color='" + color + '\'' +
	                ", source='" + source + '\'' +
	                ", asin='" + asin + '\'' +
	                ", productIndex=" + productIndex +
	                '}';
	    }
	}