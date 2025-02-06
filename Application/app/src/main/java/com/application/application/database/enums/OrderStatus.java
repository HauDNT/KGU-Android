package com.application.application.database.enums;

public enum OrderStatus {
    PENDING(0),
    DELIVERED(1),
    CANCELLED(2),
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
