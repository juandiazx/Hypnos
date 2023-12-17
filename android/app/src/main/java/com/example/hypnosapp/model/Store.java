package com.example.hypnosapp.model;

import java.util.Date;

public class Store {
    private String address;
    private double[] location; // Punto geográfico como un array de latitud y longitud
    private String name;
    private String picture;
    private String web;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    //constructor vacío:
    public Store() {}

    //constructor:
    public Store(double[] location, String name, String web) {

        this.location = location;
        this.name = name;
        this.web = web;
    }
}

