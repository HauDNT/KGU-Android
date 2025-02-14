package com.application.application.model;

public class Product {
    private String name;
    private String description;
    private String price;
    private int imageResource; // Có thể để trống nếu không có hình ảnh

    // Constructor
    public Product(String name, String description, String price, int imageResource) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource; // Có thể trả về 0 hoặc -1 nếu không có hình ảnh
    }
}