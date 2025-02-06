package com.application.application.model;

public class OrderItem {
    private int id;
    private int food_id;
    private int order_id;
    private int quantity;
    private double totalPrice;

    public OrderItem() {
    }

    public OrderItem(int id, int food_id, int order_id, int quantity, double totalPrice) {
        this.id = id;
        this.food_id = food_id;
        this.order_id = order_id;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFood_id() {
        return food_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }
}
