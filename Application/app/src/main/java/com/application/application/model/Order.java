package com.application.application.model;

import com.application.application.database.enums.OrderStatus;

public class Order {
    int id;
    String name;
    OrderStatus status;
    String description;
    String delivery_at;
    String created_at;
    String updated_at;
    int userId;
    double total;

    public Order(int id, String name, OrderStatus status, String description, String delivery_at, String created_at, String updated_at, int userId) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.delivery_at = delivery_at;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDelivery_at() {
        return delivery_at;
    }

    public void setDelivery_at(String delivery_at) {
        this.delivery_at = delivery_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotal() { return total; }

    public void setTotal(double total) { this.total = total; }
}
