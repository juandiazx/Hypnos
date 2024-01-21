package com.example.hypnosapp.model;

import java.util.List;
import java.util.Map;

public class User {
    private String birth;
    private String email;
    private String name;
    private String picture;
    private List<String> problems;
    private Preferences preferences;

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

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public static class Preferences {
        private Map<String, Object> clockSettings; // Map for clockSettings
        private String lightSettings;
        private Map<String, Object> goals; // Map for goals

        public Map<String, Object> getClockSettings() {
            return clockSettings;
        }

        public void setClockSettings(Map<String, Object> clockSettings) {
            this.clockSettings = clockSettings;
        }

        public String getLightSettings() {
            return lightSettings;
        }

        public void setLightSettings(String lightSettings) {
            this.lightSettings = lightSettings;
        }

        public Map<String, Object> getGoals() {
            return goals;
        }

        public void setGoals(Map<String, Object> goals) {
            this.goals = goals;
        }
    }

    public User(){}

    public User(String birth, String email, String name, String picture, List<String> problems, Preferences preferences) {
        this.birth = birth;
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.problems = problems;
        this.preferences = preferences;
    }
}
