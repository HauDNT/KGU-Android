package com.application.application;

public class Product {
    private String name;
    private String description;
    private String price;
    private int image;

    public Product(String name, String description, String price, int image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public int getImage() {
        return image;
    }

    public boolean getImageResource() {
        // You need to implement the logic here.
        // For example, you might return true if the image exists, or false otherwise.
        // Here's a placeholder implementation:
        return image != 0;  // Assuming 0 means no image, change as needed.
    }
}