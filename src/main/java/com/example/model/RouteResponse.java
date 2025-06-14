package com.example.model;

import java.util.List;

public class Route {
    private String id;
    private String title;
    private double budget;
    private int days;
    private List<String> attractions;
    private String transportation;
    private String accommodation;
    private String dining;
    private String details;

    // 构造方法、getter和setter
    public Route() {}

    public Route(String id, String title, double budget, int days,
                 List<String> attractions, String transportation,
                 String accommodation, String dining, String details) {
        this.id = id;
        this.title = title;
        this.budget = budget;
        this.days = days;
        this.attractions = attractions;
        this.transportation = transportation;
        this.accommodation = accommodation;
        this.dining = dining;
        this.details = details;
    }

    // 省略getter和setter...
}