package com.example.hamyduyen_2280600508.model;

public enum OrderStatus {
    PENDING("Chờ xử lý"),
    CONFIRMED("Đã xác nhận"),
    SHIPPED("Đã gửi"),
    DELIVERED("Đã giao"),
    CANCELLED("Đã hủy");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
