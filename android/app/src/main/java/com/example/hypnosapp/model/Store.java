package com.example.hypnosapp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Store {
    private String address;
    private List<Double> location; // Punto geográfico como un array de latitud y longitud
    private String name;
    private String picture;
    private String web;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {

        List<Double> locationDouble = new ArrayList<>();

        for(String string : location){
            double doubleValue = Double.parseDouble(string);
            locationDouble.add(doubleValue);
        }

        this.location = locationDouble;
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
    public Store(List<String> location, String name, String web) {

        List<Double> locationDouble = new ArrayList<>();

        for(String string : location){
            double doubleValue = Double.parseDouble(string);
            locationDouble.add(doubleValue);
        }

        this.location = locationDouble;
        this.name = name;
        this.web = web;
    }
}

