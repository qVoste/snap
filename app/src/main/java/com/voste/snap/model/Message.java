package com.voste.snap.model;

public class Message {
    private String id;
    private final String ownerId;
    private final String text;
    private final String date;

    public Message(String id, String ownerId, String text, String date) {
        this.id = id;
        this.ownerId = ownerId;
        this.text = text;
        this.date = date;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public String getText() {
        return text;
    }
    public String getDate() {
        return date;
    }
}
