package com.example.electrick;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class Model implements Serializable {
    private List<String> features;
    private Double seats;
    private String brand;

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    private String id;
    private String name;
    private String photo;
    private Double range;
    private Double price;

    public Model(){

    }
    public Model(String id, String brand, String name, String photo, Double range, Double seats, Double price) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.photo = photo;
        this.range = range;
        this.seats =  seats;
        this.price = price;
    }

    public Double getRange() {
        return range;
    }

    public String getBrand() {
        return brand;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public Double getSeats() {
        return seats;
    }

    public double getPrice() {return price;}


    public void setSeats(Double seats) {
        this.seats = seats;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setRange(Double range) {
        this.range = range;
    }

    public void setPrice(Double price) {this.price = price; }
}
