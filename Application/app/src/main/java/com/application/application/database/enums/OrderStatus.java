package com.application.application.database.enums;

public enum OrderStatus {
    PENDING(0),     // Đang xử lý
    DELIVERED(1),   // Đã giao
    CANCELLED(2),   // Đã huỷ
    ;

    private final int statusValue;

    OrderStatus(int statusValue) {
        this.statusValue = statusValue;
    }

    public int getStatusValue() {
        return statusValue;
    }

    public static OrderStatus fromValue(int integerValue) {
        for (OrderStatus status : values()) {
            if (status.statusValue == integerValue) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status value: " + integerValue);
    }
}
