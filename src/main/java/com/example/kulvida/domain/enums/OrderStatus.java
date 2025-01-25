package com.example.kulvida.domain.enums;

public enum OrderStatus {

    INITIALIZED("INITIALIZED"),
    COMPLETED("COMPLETED"),
	SENT("SENT"),
	DELIVERY("DELIVERY"),
	DELIVERED("DELIVERED");

    private final String orderStatus;

    OrderStatus(String orderStatus) {
        this.orderStatus= orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
