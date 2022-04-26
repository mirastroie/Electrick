package com.example.electrick;

import com.google.type.DateTime;

import java.time.LocalDateTime;

public class Rental {
    private String userId;
    private LocalDateTime date;
    private EV ev;
    private Double totalPrice;
    private Double duration;

    public Rental(){

    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Rental(String userId, LocalDateTime date, EV ev) {
        this.userId = userId;
        this.date = date;
        this.ev = ev;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public EV getEv() {
        return ev;
    }

    public void setEv(EV ev) {
        this.ev = ev;
    }
}
