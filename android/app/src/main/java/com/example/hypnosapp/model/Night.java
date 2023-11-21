package com.example.hypnosapp.model;

import java.util.Date;

public class Night {
    private String breathing;
    private Date date;
    private int score;
    private int temperature;
    private int time;

    public String getBreathing() {
        return breathing;
    }

    public void setBreathing(String breathing) {
        this.breathing = breathing;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Night() {
    }

    public Night(Date date, String breathing, int score, int temperature, int time) {

        this.date = date;
        this.breathing = breathing;
        this.score = score;
        this.temperature = temperature;
        this.time = time;

    }

}

