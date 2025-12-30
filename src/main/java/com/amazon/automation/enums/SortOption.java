
package com.amazon.automation.enums;

public enum SortOption {

    PRICE_LOW_TO_HIGH("price-asc-rank", true),
    PRICE_HIGH_TO_LOW("price-desc-rank", false);

    private final String value;
    private final boolean ascending;

    SortOption(String value, boolean ascending) {
        this.value = value;
        this.ascending = ascending;
    }

    public String getValue() {
        return value;
    }

    public boolean isAscending() {
        return ascending;
    }
}
