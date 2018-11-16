package com.example.christian.chatapp.Model;

import java.util.HashMap;

public class User {

    private String id;
    private String username;
    private String imageURL;
    private HashMap<String, Boolean> groups = new HashMap<>();

    public User(String id, String username, String imageURL, HashMap<String, Boolean> groups) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.groups = groups;
    }

    public User(String id, String username, String imageURL) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
    }

    public User(String id) {
        this.id = id;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public HashMap<String, Boolean> getGroups() {
        return groups;
    }

    public void setGroups(HashMap<String, Boolean> groups) {
        this.groups = groups;
    }
}
