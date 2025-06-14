package com.example.model;

public class spot {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private double ticketPrice;
    private String openingHours;
    private String location;

    // 构造方法、getter和setter
    public Attraction() {}

    public Attraction(String id, String name, String description, String imageUrl,
                      double ticketPrice, String openingHours, String location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.ticketPrice = ticketPrice;
        this.openingHours = openingHours;
        this.location = location;
    }

    // 省略getter和setter...
}