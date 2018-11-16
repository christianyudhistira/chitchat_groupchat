package com.example.christian.chatapp.Model;

public class Thread {
    private Long creationDate;
    private int type;
    private String username;

    public Thread() {
    }

    public Thread(Long creationDate, int type, String username) {
        this.creationDate = creationDate;
        this.type = type;
        this.username = username;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
