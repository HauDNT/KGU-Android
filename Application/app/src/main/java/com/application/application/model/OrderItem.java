package com.application.application.model;

import com.application.application.database.DatabaseHelper;

public class OrderItem {
    private int id;
    private int food_id;
    private int order_id;
    private int quantity;
    private double totalPrice;
    private String food_name;

    public OrderItem() {
    }

    public OrderItem(int id, int food_id, int order_id, int quantity, double totalPrice) {
        this.id = id;
        this.food_id = food_id;
        this.order_id = order_id;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public OrderItem(int id, int food_id, String food_name, int order_id, int quantity, double totalPrice) {
        this.id = id;
        this.food_id = food_id;
        this.food_name = food_name;
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

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }
}
