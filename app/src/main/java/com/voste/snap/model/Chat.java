package com.voste.snap.model;

public class Chat {
    String chat_id;
    String chat_name;
    String userId1;
    String UserId2;

    public Chat(String chat_id, String chat_name, String userId1, String getUserId2) {
        this.chat_id = chat_id;
        this.chat_name = chat_name;
        this.userId1 = userId1;
        this.UserId2 = getUserId2;
    }

    public String getChat_id() {
        return chat_id;
    }
    public String getChat_name() {
        return chat_name;
    }
    public String getUserId1() {
        return userId1;
    }
    public String getUserId2() {
        return UserId2;
    }
}
