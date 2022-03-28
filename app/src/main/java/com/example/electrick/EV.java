package com.example.electrick;

import java.io.Serializable;

public class EV implements Serializable {
    private String id;
    private Double capacity;
    private String location;
    private Model model;

    public EV() {

    }
    public EV(String id, Double capacity, String location, Model model) {
        this.id = id;
        this.capacity = capacity;
        this.location = location;
        this.model = model;
    }

    public Double getCapacity() {
        return capacity;
    }

    public String getLocation() {
        return location;
    }

    public Model getModel() {
        return model;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
