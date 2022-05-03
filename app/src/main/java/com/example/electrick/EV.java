package com.example.electrick;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

public class EV implements Serializable, ClusterItem {
    private String id;
    private Double battery;
    private LatLng location;
    private Model model;

    public EV() {

    }
    public EV(String id, Double battery, LatLng location, Model model) {
        this.id = id;
        this.battery = battery;
        this.location = location;
        this.model = model;
    }

    public Double getBattery() {
        return battery;
    }

    public Model getModel() {
        return model;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBattery(Double battery) {
        this.battery = battery;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return location;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }

    public String getId() {
        return id;
    }
}
