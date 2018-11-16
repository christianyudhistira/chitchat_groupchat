package com.example.christian.chatapp.Model;

public class Message {

    private String sender;
    private String payload;
    private Long timestamp;

    public Message(String sender, String payload) {
        this.sender = sender;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
    }

    public Message() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
