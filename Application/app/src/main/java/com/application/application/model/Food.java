package com.application.application.model;

public class Food {
    private int id;
    private String name;
    private String description;
    private Integer price;
    private int status; //0: Còn hàng, 1: Hết hàng
    private String imageUrl;

    //Constructor
    public Food(int id, String name, String description, Integer price, int status, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}