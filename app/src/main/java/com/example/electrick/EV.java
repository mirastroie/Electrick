package com.example.electrick;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

public class EV implements Serializable, ClusterItem {
    private String id;
    private Double capacity;
    private LatLng location;
    private Model model;

    public EV() {

    }
    public EV(String id, Double capacity, LatLng location, Model model) {
        this.id = id;
        this.capacity = capacity;
        this.location = location;
        this.model = model;
    }

    public Double getCapacity() {
        return capacity;
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
}
