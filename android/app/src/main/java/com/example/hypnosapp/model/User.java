package com.example.hypnosapp.model;

import java.util.Map;
import java.util.List;

public class User {
    private String birth;
    private String email;
    private Map<String, Object> goals; // El mapa goals ahora contiene goBedTime, time y wakeUpTime
    private String name;
    private String picture;
    private List<String> problems;
    private Map<String, Boolean> settings;

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Object> getGoals() {
        return goals;
    }

    public void setGoals(Map<String, Object> goals) {
        this.goals = goals;
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

    public List<String> getProblems() {
        return problems;
    }

    public void setProblems(List<String> problems) {
        this.problems = problems;
    }

    public Map<String, Boolean> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, Boolean> settings) {
        this.settings = settings;
    }
}

