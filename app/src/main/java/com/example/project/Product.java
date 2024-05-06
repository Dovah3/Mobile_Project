package com.example.project;

public class Product {
    private String name;
    private String price;
    private String imageResource;

    public Product(String name, String price,String imageResource) {
        this.name = name;
        this.price = price;
        this.imageResource = imageResource;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getImageResource() { return imageResource; }
}