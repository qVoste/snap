package com.voste.snap.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.voste.snap.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatCacheHelper {

    private static final String PREF_NAME = "chat_cache";
    private static final String CHATS_KEY = "chats_list";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public ChatCacheHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Сохранение чатов
    public void saveChats(List<Chat> chats) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(chats);
        editor.putString(CHATS_KEY, json);
        editor.apply();
    }

    // Загрузка чатов
    public List<Chat> loadChats() {
        String json = sharedPreferences.getString(CHATS_KEY, null);
        if (json != null) {
            Chat[] chatArray = gson.fromJson(json, Chat[].class);
            return new ArrayList<>(List.of(chatArray));
        }
        return new ArrayList<>();
    }

    // Очистка кеша
    public void clearCache() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(CHATS_KEY);
        editor.apply();
    }
}
