package com.voste.snap.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import com.voste.snap.model.User;

public class ChatUtil {
    public static void createChat(User user){
        //получение айди пользователя
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        HashMap<String, String> chatInfo = new HashMap<>();
        chatInfo.put("user1", uid);
        chatInfo.put("user2", user.uid);
        //получение айди чата
        String chatId = generateChatId(uid, user.uid);
        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
                .setValue(chatInfo);
        addChatIdToUser(uid, chatId);
        addChatIdToUser(user.uid, chatId);
    }
    //генерация айди чата
    private static String generateChatId(String userId1, String userId2){
        String sumUser1User2 = userId1+userId2;
        char[] charArray = sumUser1User2.toCharArray();
        Arrays.sort(charArray);

        return new String(charArray);
    }
    //сохранение в список чатов
    private static void addChatIdToUser(String uid, String chatId){
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .child("chats").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String chats = Objects.requireNonNull(task.getResult().getValue()).toString();
                        String chatsUpd = addIdToStr(chats, chatId);

                        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                                .child("chats").setValue(chatsUpd);
                    }
                });
    }
    //добавление айди чата в строку чатов
    private static String addIdToStr(String str, String chatId){
        str += (str.isEmpty()) ? chatId : (","+chatId);
        return str;
    }
}
