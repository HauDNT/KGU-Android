package com.application.application.database.enums;

public enum FoodStatus {
    IN_STOCK(0),   // Còn hàng
    OUT_STOCK(1),    // Hết hàng
    DISABLE(2),     // Đã tắt
    ;

    private final int statusValue;

    FoodStatus(int statusValue) {
        this.statusValue = statusValue;
    }

    public int getStatusValue() {
        return statusValue;
    }

    public static FoodStatus fromValue(int integerValue) {
        for (FoodStatus status: values()) {
            if (status.statusValue == integerValue) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown status value: " + integerValue);
    }
}
