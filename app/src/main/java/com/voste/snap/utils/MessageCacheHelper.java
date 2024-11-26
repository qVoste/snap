package com.voste.snap.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.voste.snap.model.Message;

import java.util.List;

public class MessageCacheHelper {

    private static final String PREF_NAME = "message_cache";
    private static final String MESSAGES_KEY_PREFIX = "messages_"; // ключ для каждого чата

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public MessageCacheHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Сохранение сообщений
    public void saveMessages(String chatId, List<Message> messages) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(messages);
        editor.putString(MESSAGES_KEY_PREFIX + chatId, json);
        editor.apply();
    }

    // Загрузка сообщений
    public List<Message> loadMessages(String chatId) {
        String json = sharedPreferences.getString(MESSAGES_KEY_PREFIX + chatId, null);
        if (json != null) {
            Message[] messageArray = gson.fromJson(json, Message[].class);
            return List.of(messageArray);
        }
        return null;
    }

    // Очистка кеша сообщений
    public void clearCache(String chatId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(MESSAGES_KEY_PREFIX + chatId);
        editor.apply();
    }
}
