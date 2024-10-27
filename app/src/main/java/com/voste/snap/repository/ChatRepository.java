package com.voste.snap.repository;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;

public class ChatRepository {
    private DatabaseReference chatsRef;

    public ChatRepository() {
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
    }

    public void createChatWithUser(String userNickname, String currentUserId, UserRepository userRepository, ChatCreationCallback callback) {
        userRepository.findUserByNickname(userNickname, new UserRepository.FirebaseCallback() {
            @Override
            public void onCallback(String userId) {
                if (userId != null) {
                    // UID текущего пользователя
                    String userId1 = currentUserId;
                    String userId2 = userId;

                    // Создаём новый чат
                    String chatId = chatsRef.push().getKey(); // Генерация уникального ID для чата

                    HashMap<String, Object> chatData = new HashMap<>();
                    chatData.put("chatId", chatId);
                    chatData.put("userIds", Arrays.asList(userId1, userId2)); // Сохраняем два UID

                    chatsRef.child(chatId).setValue(chatData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("CHAT_CREATION", "Чат успешно создан.");
                            callback.onSuccess();
                        } else {
                            Log.e("CHAT_CREATION", "Ошибка создания чата.");
                            callback.onFailure();
                        }
                    });
                } else {
                    Log.e("USER_NOT_FOUND", "Пользователь не найден.");
                    callback.onFailure();
                }
            }
        });
    }

    public interface ChatCreationCallback {
        void onSuccess();
        void onFailure();
    }
}
