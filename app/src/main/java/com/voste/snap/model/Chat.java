package com.voste.snap.model;

public class Chat {
    private String chat_id;
    private String chat_name;
    private String userId1;
    private String userId2;

    public Chat(String chat_id, String chat_name, String userId1, String userId2) {
        this.chat_id = chat_id;
        this.chat_name = chat_name;
        this.userId1 = userId1;
        this.userId2 = userId2;
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
        return userId2;
    }

    // Метод для получения ID собеседника
    public String getOtherUserId(String currentUserId) {
        if (userId1.equals(currentUserId)) {
            return userId2; // Если текущий пользователь - userId1, возвращаем userId2
        } else if (userId2.equals(currentUserId)) {
            return userId1; // Если текущий пользователь - userId2, возвращаем userId1
        }
        return null; // Если текущий пользователь не найден (что маловероятно)
    }
}
